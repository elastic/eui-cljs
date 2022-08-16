(ns eui.test.patch-random
  (:require ["@elastic/eui/lib/test/patch_random.js" :as eui]))

(def unpatchRandom eui/unpatchRandom)

(def patchRandom eui/patchRandom)
