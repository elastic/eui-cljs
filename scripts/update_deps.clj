(ns update-deps
  (:require [cheshire.core :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as s]))

(defn update-release [version]
  (let [release (edn/read-string (slurp "release.edn"))]
    (pp/pprint (assoc release :version (str version "-SNAPSHOT"))
               (io/writer "release.edn"))
    version))

(defn update-package-json [version]
  (let [current (json/parse-string (slurp "package.json"))]
    (spit "package.json"
          (json/generate-string (-> current
                                    (assoc "version" version)
                                    (assoc-in ["dependencies" "@elastic/eui"] version))
                                {:pretty true}))
    version))

(defn update-deps-cljs [version]
  (let [current (json/parse-string (slurp "package.json"))]
    (spit "src/deps.cljs"
          (with-out-str (pp/pprint {:npm-deps (get current "dependencies")})))
    version))

(defn update-readme [version]
  (let [readme (slurp "README.md")]
    (spit "README.md"
          (s/replace-first readme #"v\[(.+)\]" (str "v[" version "]")))
    version))

(defn -main [eui-version]
  (-> eui-version
      (update-package-json)
      (update-deps-cljs)
      (update-readme)
      (update-release)))
