(ns eui.eui-markdown-link-validator
  (:require ["@elastic/eui/lib/components/markdown_editor/plugins/markdown_link_validator.js" :as eui]))

(def euiMarkdownLinkValidator eui/euiMarkdownLinkValidator)

(def validateUrl eui/validateUrl)

(def mutateLinkToText eui/mutateLinkToText)
