(ns eui.node-to-html
  (:require ["@elastic/eui/lib/components/code/utils.js" :as eui]))

(def nodeToHtml eui/nodeToHtml)

(def DEFAULT_LANGUAGE eui/DEFAULT_LANGUAGE)

(def SUPPORTED_LANGUAGES eui/SUPPORTED_LANGUAGES)

(def isAstElement eui/isAstElement)

(def checkSupportedLanguage eui/checkSupportedLanguage)

(def getHtmlContent eui/getHtmlContent)

(def parseLineRanges eui/parseLineRanges)

(def highlightByLine eui/highlightByLine)
