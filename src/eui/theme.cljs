(ns eui.theme
  (:require-macros [eui.theme :refer [load-file load-json]]))

(def themes
  {:light
   {:css (load-file "dist/eui_theme_amsterdam_light.min.css")
    :values (load-json "dist/eui_theme_amsterdam_light.json")}

   :dark
   {:css (load-file "dist/eui_theme_amsterdam_dark.min.css")
    :values (load-json "dist/eui_theme_amsterdam_dark.json")}})
