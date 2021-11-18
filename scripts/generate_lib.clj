(ns generate-lib
  (:require [camel-snake-kebab.core :as csk]
            [cheshire.core :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as s]))

(def root "node_modules/")
(def dir-name "@elastic/eui/lib")

(defn computed-file-props [{:keys [path exports]}]
  (let [[_ path]      (re-find #"node_modules/(.+)" path)
        [_ file-type] (re-find (re-pattern (str dir-name "/([a-z]+)")) path)
        file-name'    (or (some (fn [export]
                                  (when (s/starts-with? export "Eui")
                                    export))
                                (sort exports))
                          (first exports))
        icon?         (= file-name' "icon")
        component?    (s/starts-with? file-name' "Eui")
        other?        (and (not icon?) (not component?) (not= "components" file-type))
        icon-name     (second (re-find #"/([\w\_\-]+)\.js" path))
        icon-var-name (csk/->camelCase icon-name)
        file-name     (cond
                        component?
                        (second (re-find #"Eui(.+)" file-name'))

                        icon?
                        (str "icon_" icon-name)

                        other?
                        (-> path (s/split #"/") last (s/split #"\.") first)

                        :else
                        file-name')
        kebab-case    (csk/->kebab-case file-name)
        snake-case    (csk/->snake_case file-name)
        file          (str "src/eui/" (when other? (str file-type "/")) snake-case ".cljs")
        out           (str "(ns eui." (when other? (str file-type ".")) kebab-case "\n"
                           "  (:require [\"" path "\" :as eui]))")
        out           (concat [out] (mapv #(str "(def " (if icon? icon-var-name %) " eui/" % ")") exports))
        file-contents (str (s/join "\n\n" out) "\n")]
    {:file-path     path
     :file-type     file-type
     :file-contents file-contents
     :icon?         icon?
     :component?    component?
     :other?        other?
     :out-path      file
     :namespace     (str "eui." (when other? (str file-type ".")) kebab-case)
     :exports       (mapv (fn [export]
                            (if icon? icon-var-name export)) exports)
     :kebab-case    kebab-case
     :snake-case    snake-case}))

(defn generate-file [file]
  (let [{:keys [out-path file-contents icon? component?]} (computed-file-props file)]
    (io/make-parents out-path)
    (spit out-path file-contents)))

(defn parse-exports
  "Takes a tuple of a file-path and the export line
   and parses the variables we need to export for this file
   returns a map"
  [[file-path export-lines]]
  (when (not (empty? export-lines))
    (try
      (let [exports (mapcat
                     (fn [export-line]
                       (let [exports (->> (re-seq #"exports\.([a-zA-Z0-9]+)" export-line)
                                          (mapv second))]
                         exports))
                     export-lines)]
        {:path    file-path
         :exports (into #{} exports)})
      (catch Exception e
        {:path    file-path
         :exports export-lines
         :error   (.getMessage e)}))))

(defn parse-export-line
  "Somewhere in the beginning of the source file is an exports declaration, like:
  `exports.EuiToggle = exports.TYPES = void 0;`
  This function finds and returns that line for further processing"
  [file]
  (with-open [rdr (io/reader file)]
    (try
      [file
       (reduce (fn [acc line]
                 (if (s/starts-with? line "exports.")
                   (conj acc line)
                   acc))
               []
               (line-seq rdr))]
      (catch Exception e
        (str "Problem parsing: " file)))))

(defn component-files
  ([]
   (component-files (str root dir-name)))
  ([dir-name]
   (let [dir   (io/file dir-name)
         files (file-seq dir)]
     (reduce (fn [acc file]
               (let [filename (.getName file)]
                 (if (and (not (.isDirectory file))
                          (not= filename "index.js")
                          (s/ends-with? filename ".js"))
                   (conj acc (.getPath file))
                   acc)))
             []
             files))))

(defn move [from to]
  (let [dir   (io/file from)
        files (file-seq dir)]
    (doseq [file files]
      (let [filename (.getName file)]
        (when (not (.isDirectory file))
          (io/copy file (io/file (str to "/" filename))))))))

(defn generate-component-doc [files]
  (let [out ["## Component table\n"
             "This table maps variable names to the file on disk. So if you need to just manually import the raw file it's easy to track down here.\n"
             "| Variables exported | Clojurescript Namespace | Path on Disk |"
             "| --------- | ---------------------------- | ------------------ |"]
        out (s/join "\n" (into out (->> files
                                        (mapv (fn [file]
                                                (let [{:keys [exports namespace file-path icon? component?]} (computed-file-props file)
                                                      exports-str                                            (s/join "<br>" (mapv #(str "`" % "`") exports))]
                                                  (str "| " exports-str " | `" namespace "` | `" (str root file-path) "` |" )))))))]
    (spit "components.md" out)))

(defn -main []
  (try
    (let [files (->> (component-files)
                     (mapv parse-export-line)
                     (mapv parse-exports)
                     (filterv (comp not nil?)))]
      (generate-component-doc files)
      (doseq [file files]
        (generate-file file))
      (move "overwrites" "src/eui"))
    (catch Exception e
      (println (.getMessage e)))))
