# A Clojurescript wrapper for EUI v[81.0.0](https://elastic.github.io/eui/#/package/changelog)

Smooths out the experience of using EUI from Clojurescript

[![Clojars Project](https://img.shields.io/clojars/v/co.elastic/eui-cljs.svg)](https://clojars.org/co.elastic/eui-cljs)

## Quickstart

* Add `elastic/eui-cljs` to your project
* `npm install -g yarn` EUI Requires yarn
* Call `(eui.theme/init!)` in your application's main function

Check out the sample application in the `examples` directory to get a feel for how to use it, and then head over to the [official EUI docs site](https://eui.elastic.co/) to start learning!

There is a formula for importing components you might be looking at in the [docs](https://eui.elastic.co/). Given an EUI component, something like `EuiLoadingSpinner`, the namespace always starts with `eui.` and then convert the PascalCase name to kebab-case `eui.loading-spinner` followed by a `:refer` using the original component name. Putting it all together `<EuiLoadingSpinner />` => `[eui.loading-spinner :refer [EuiLoadingSpinner]]`. If for some reason that doesn't work, view the [components table](./components.md) to see exactly where we are mapping variables you come across.

## Motivation

It's critical you import individual components to ensure your final bundle only includes code you rely on. Going down that path, you'll start to feel the friction of having to track down exactly which file any given EUI variable lives in.

For instance, when checking out [the EUI docs site](https://eui.elastic.co), you'll come across example code like this:

```javascript
import {
  EuiBadge,
  EuiFlexItem,
  EuiFlexGroup,
  EuiSpacer,
  EuiSwitch,
  EuiText,
  EuiTitle,
} from '@elastic/eui';
```

When importing directly from `node_modules/@elastic/eui` (without this library), the import section looks like:

```clojure
(ns cake.core
  (:require ["@elastic/eui/lib/components/button" :refer [EuiButton]]
            ["@elastic/eui/lib/components/button/button_icon" :refer [EuiButtonIcon]]
            ["@elastic/eui/lib/components/form/field_text" :refer [EuiFieldText]]
            ["@elastic/eui/lib/components/form/form_row" :refer [EuiFormRow]]
            ["@elastic/eui/lib/components/form/select" :refer [EuiSelect]]
            ["@elastic/eui/lib/components/form/text_area" :refer [EuiTextArea]]
            ["@elastic/eui/lib/components/horizontal_rule" :refer [EuiHorizontalRule]]
            ["@elastic/eui/lib/components/loading/loading_spinner" :refer [EuiLoadingSpinner]]
            ["@elastic/eui/lib/components/text" :refer [EuiText]]))
```

Because the path isn't related to the component name, you will need to grep through the directory to find where the variable lives which quickly becomes tedious and painful.

This project automates the generation of a Clojurescript wrapper which creates namespaces for each component in EUI with a predictable namespace that maps to the component name (`EuiPageBodyContent => eui.page-body-content`). It also generates a [components table](./components.md) that maps variable names to their original file on disk, and the Clojurescript namespace that they live in.

What it looks like to use this library:

```clojure
(ns cake.core
  (:require [eui.button :refer [EuiButton]]
            [eui.button-icon :refer [EuiButtonIcon]]
            [eui.field-text :refer [EuiFieldText]]
            [eui.form-row :refer [EuiFormRow]]
            [eui.select :refer [EuiSelect]]
            [eui.text-area :refer [EuiTextArea]]
            [eui.horizontal-rule :refer [EuiHorizontalRule]]
```

We are also able to overwrite certain components without the end-developer's knowledge which keeps the experience unified. For example, we needed to port the text field components so they would be compatible with Reagent's async-rendering. Those components live in the overwrites directory.

## Styling/Theming

The sample application in the examples directory uses the following strategy:

* In the application's main function call `(eui.theme/init!)`, This will add the required styles and fonts to the head of your document.
* You can use `(eui.theme/set-theme! (or :dark :light))` to set the correct styles, like in an event handler

## Note on Icons

The Google Closure Compiler, advanced as it is, does not currently support async Javascript imports which EUI utilizes heavily for rendering icons in components. The work around involves calling their escape-hatch `appendIconComponentCache` function with a resolver map like:

```clojure
(ns my-app.icon-resolver
  (:require
   [eui.icon :refer [appendIconComponentCache]]
   [eui.icon-apps :refer [apps]]
   [eui.icon-arrow-down :refer [arrowDown]]
   [eui.icon-arrow-up :refer [arrowUp]]
   [eui.icon-cross :refer [cross]]
   [eui.icon-link :refer [link]]
   [eui.icon-search :refer [search]]))

(def icon-map
  #js {"search"    search
       "cross"     cross
       "arrowDown" arrowDown
       "link"      link
       "arrowUp"   arrowUp
       "apps"      apps})

(appendIconComponentCache icon-map)
```

And then import the namespace so the `appendIconComponentCache` functions runs on app starup.

Check the [pr](https://github.com/elastic/eui/pull/3481) and the [issue](https://github.com/elastic/eui/issues/2973) explaining things further.

## License

[Dual-licensed under Elastic v2 and Server Side Public License, v 1][license] Read the [FAQ][faq] for details.

[license]: LICENSE.txt
[faq]: https://github.com/elastic/eui/blob/main/FAQ.md
