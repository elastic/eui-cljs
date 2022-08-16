(ns eui.test.component-helpers
  (:require ["@elastic/eui/lib/test/rtl/component_helpers.js" :as eui]))

(def waitForEuiPopoverOpen eui/waitForEuiPopoverOpen)

(def waitForEuiToolTipHidden eui/waitForEuiToolTipHidden)

(def waitForEuiToolTipVisible eui/waitForEuiToolTipVisible)

(def waitForEuiPopoverClose eui/waitForEuiPopoverClose)
