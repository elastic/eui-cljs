(ns eui.field-search
  (:require ["classnames" :default classNames]
            [eui.form-control-layout :refer [EuiFormControlLayout]]
            [eui.validatable-control :refer [EuiValidatableControl]]
            [reagent.core :as r]))

(def custom-props
  [:className
   :id
   :name
   :placeholder
   :isInvalid
   :fullWidth
   :isLoading
   :inputRef
   :incremental
   :compressed
   :onSearch
   :isClearable
   :append
   :prepend])

(defn EuiFieldSearch* []
  (let [state   (r/atom {})
        set-ref #(swap! state assoc :input-element %)]
    (fn []
      (let [{:keys [className
                    id
                    name
                    placeholder
                    isInvalid
                    fullWidth
                    isLoading
                    inputRef
                    incremental
                    compressed
                    onSearch
                    isClearable
                    value
                    onKeyUp
                    append
                    disabled
                    readOnly
                    onChange
                    prepend]
             :or   {isInvalid   false
                    fullWidth   false
                    isLoading   false
                    incremental false
                    compressed  false
                    isClearable true}
             :as   props} (r/props (r/current-component))
            on-key-up     (fn [e incremental on-search]
                            (when onKeyUp
                              (onKeyUp e)
                              (when-not (.-defaultPrevented e)
                                (when (and onSearch
                                           (not= (.-key e)))))))
            on-clear      (fn [e]
                            (onChange #js {:target {:value ""}}))
            rest          (apply dissoc props custom-props)
            classes       (classNames className
                                      "euiFieldSearch"
                                      #js{"euiFieldText--fullWidth"  fullWidth
                                          "euiFieldText--compressed" compressed
                                          "euiFieldText--inGroup"    (or prepend append)
                                          "euiFieldText-isLoading"   isLoading})]
        [:> EuiFormControlLayout {:icon       "search"
                                  :fullWidth  fullWidth
                                  :isLoading  isLoading
                                  :clear      (when (and isClearable
                                                         value
                                                         (not readOnly)
                                                         (not disabled))
                                                {:onClick on-clear})
                                  :compressed compressed
                                  :prepend    prepend
                                  :append     append}
         [:> EuiValidatableControl
          {:isInvalid isInvalid}
          [:input (merge {:type        "search"
                          :id          id
                          :name        name
                          :placeholder placeholder
                          :class       classes
                          :value       value
                          :on-key-up   #(on-key-up % incremental onSearch)
                          :ref         set-ref} rest)]]]))))

(def EuiFieldSearch (r/reactify-component EuiFieldSearch*))
