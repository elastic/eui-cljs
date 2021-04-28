(ns eui.field-text
  (:require ["classnames" :default classNames]
            [eui.form-control-layout :refer [EuiFormControlLayout]]
            [eui.validatable-control :refer [EuiValidatableControl]]
            [reagent.core :as r]))

(def custom-props
  [:id
   :name
   :placeholder
   :value
   :className
   :icon
   :isInvalid
   :inputRef
   :fullWidth
   :isLoading
   :compressed
   :prepend
   :append
   :readOnly
   :controlOnly])

(defn EuiFieldText* []
  (let [{:keys [id
                name
                placeholder
                value
                className
                icon
                isInvalid
                inputRef
                fullWidth
                isLoading
                compressed
                prepend
                append
                readOnly
                controlOnly]
         :or   {isInvalid false}
         :as   props} (r/props (r/current-component))
        rest          (apply dissoc props custom-props)
        classes       (classNames className
                                  "euiFieldText"
                                  #js{"euiFieldText--withIcon"   icon
                                      "euiFieldText--fullWidth"  fullWidth
                                      "euiFieldText--compressed" compressed
                                      "euiFieldText--inGroup"    (or prepend append)
                                      "euiFieldText-isLoading"   isLoading})
        control       [(r/adapt-react-class EuiValidatableControl)
                       {:isInvalid isInvalid}
                       [:input (merge {:type        "text"
                                       :id          id
                                       :name        name
                                       :placeholder placeholder
                                       :class       classes
                                       :value       value
                                       :ref         inputRef
                                       :readOnly    readOnly} rest)]]]
    (if controlOnly
      control
      [:> EuiFormControlLayout {:icon       icon
                                :fullWidth  fullWidth
                                :isLoading  isLoading
                                :compressed compressed
                                :readOnly   readOnly
                                :prepend    prepend
                                :append     append
                                :inputId    id}
       control])))

(def EuiFieldText (r/reactify-component EuiFieldText*))
