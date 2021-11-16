(ns eui.theme
  (:require-macros [eui.theme :refer [load-file load-json]])
  (:require [goog.dom :as gdom]))

(def default :dark)

(def font-href "https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Roboto+Mono:ital,wght@0,400;0,700;1,400;1,700&display=swap")

(def themes
  {:light
   {:css (load-file "dist/eui_theme_amsterdam_light.min.css")
    :values (load-json "dist/eui_theme_amsterdam_light.json")}

   :dark
   {:css (load-file "dist/eui_theme_amsterdam_dark.min.css")
    :values (load-json "dist/eui_theme_amsterdam_dark.json")}})

(defn next-theme
  "Returns the opposite theme for the current value
   useful when toggling between light/dark"
  ([] (next-theme default))
  ([current-theme]
   (current-theme {:light :dark :dark :light})))

(defn load-fonts!
  "Creates two link nodes in the head section of the document
   which load the Inter font and styling"
  []
  (let [head (.querySelector js/document "head")
        font-link (gdom/createElement "link")]
    (if head
      (do
        (gdom/setProperties font-link #js {"rel" "stylesheet"
                                           "href" font-href})
        (gdom/appendChild head font-link))
      (throw (js/Error. "load-fonts! error: No <head></head> element found for <link> elements")))))

(defn set-theme!
  "Sets the CSS in the document"
  ([] (set-theme! default))
  ([theme-name]
   (let [head (.querySelector js/document "head")
         existing (.querySelector js/document "#eui-theme-tag")
         style (or existing
                 (let [el (gdom/createElement "style")]
                   (gdom/setProperties el #js {"id" "eui-theme-tag"})
                   el))]
     (gdom/setTextContent style (-> themes theme-name :css))
     (when (not existing)
       (gdom/appendChild head style)))))

(defn init!
  "Appends the theme and and loads fonts"
  ([] (init! default))
  ([theme-name]
   (set-theme! theme-name)
   (load-fonts!)))
