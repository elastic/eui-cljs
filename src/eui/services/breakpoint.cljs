(ns eui.services.breakpoint
  (:require ["@elastic/eui/lib/services/breakpoint/breakpoint.js" :as eui]))

(def isWithinBreakpoints eui/isWithinBreakpoints)

(def BREAKPOINTS eui/BREAKPOINTS)

(def isWithinMinBreakpoint eui/isWithinMinBreakpoint)

(def isWithinMaxBreakpoint eui/isWithinMaxBreakpoint)

(def BREAKPOINT_KEYS eui/BREAKPOINT_KEYS)

(def getBreakpoint eui/getBreakpoint)
