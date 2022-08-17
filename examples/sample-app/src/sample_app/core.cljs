(ns sample-app.core
  (:require [eui.button :refer [EuiButton]]
            [eui.field-text :refer [EuiFieldText]]
            [eui.form-row :refer [EuiFormRow]]
            [eui.horizontal-rule :refer [EuiHorizontalRule]]
            [eui.icon :refer [appendIconComponentCache]]
            [eui.icon-logo-elastic :refer [logoElastic]]
            [eui.page :refer [EuiPage]]
            [eui.page-body :refer [EuiPageBody]]
            [eui.page-content :refer [EuiPageContent]]
            [eui.page-content-body :refer [EuiPageContentBody]]
            [eui.page-header :refer [EuiPageHeader]]
            [eui.provider :refer [EuiProvider]]
            [eui.text :refer [EuiText]]
            [eui.theme :as theme]
            [reagent.core :as r]
            [reagent.dom :refer [render]]))

(defonce app-state (r/atom {:theme :dark
                            :page-title "EUI from ClojureScript"}))

(defn title-form []
  [:> EuiFormRow {:label "Set the page title"}
   [:> EuiFieldText {:value (:page-title @app-state)
                     :onChange (fn [e]
                                 (swap! app-state (fn [state]
                                                    (assoc state :page-title (.. e -target -value)))))}]])

(defn app []
  (let [current-theme (:theme @app-state)
        page-title (:page-title @app-state)
        next-theme (theme/next-theme current-theme)]
    [:> EuiProvider {:colorMode current-theme}
     [:> EuiPage {:paddingSize "none"}
      [:> EuiPageBody {:restrictWidth true}
       [:> EuiPageHeader {;; for this to work we need to register the icon with EUI down below
                          :iconType "logoElastic"
                          :pageTitle page-title
                          :rightSideItems [(r/as-element ;; When passing components to EUI through props (not children)
                                            ;; use r/as-element to convert the Clojurescript to callable React
                                            [:> EuiButton {:onClick (fn [_]
                                                                      (swap! app-state (fn [state]
                                                                                         (assoc state :theme next-theme)))
                                                                      (theme/set-theme! next-theme))}
                                             (str "Toggle theme: " (name next-theme))])]
                          :paddingSize "l"}]
       [:> EuiPageContent
        [:> EuiPageContentBody
         [title-form]
         [:> EuiHorizontalRule]
         [:> EuiText
          [:h3 "More info"]
          [:p "Children passed to EUI component are automatically converted thanks to Reagent interop `:>`, but passing components through props should be converted with `(r/as-element)` first :)"]]]]]]]))

(defn init []
  ;; Work around needed because the Closure compiler does not
  ;; support dynamic importing, so we need to provide a lookup
  ;; map EUI can use to find logos internally
  (appendIconComponentCache #js {"logoElastic" logoElastic})

  ;; Appends the stylesheet to document along with the amsterdam Google font
  (theme/init! (:theme @app-state))

  ;; render the app
  (render [#'app] (.getElementById js/document "root")))
