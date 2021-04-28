(ns eui.markdown-link-validator
  (:require ["@elastic/eui/lib/components/markdown_editor/plugins/markdown_link_validator.js" :as eui]))

(def markdownLinkValidator eui/markdownLinkValidator)

(def validateUrl eui/validateUrl)

(def mutateLinkToText eui/mutateLinkToText)
