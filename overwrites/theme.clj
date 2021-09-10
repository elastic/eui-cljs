(ns eui.theme
  (:require [cheshire.core :as json]))

(defmacro load-file
  [path]
  (-> (str "node_modules/@elastic/eui/" path)
      (slurp)))

(defmacro load-json
  [path]
  (-> (str "node_modules/@elastic/eui/" path)
      (slurp)
      (json/parse-string)))
