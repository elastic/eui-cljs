# A Clojurescript wrapper for EUI v[32.2.0](https://elastic.github.io/eui/#/package/changelog)

Smooths out the experience of using EUI from Clojurescript.

[![Clojars Project](https://img.shields.io/clojars/v/elastic/eui-cljs.svg)](https://clojars.org/elastic/eui-cljs)

It's currently difficult to track down exactly which file any given EUI variable lives in. Every component should be imported separately because that reduces the final bundle size.

For instance, when checking out [the EUI docs site](https://elastic.github.io/eui/#/), you'll come across example code like this:

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

When importing directly from EUI, the import section looks like:

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

There's not a convention for where a variable lives, so often you need to grep through the directory to find where the variable lives, and it can be tedious and frustrating after regular use.

This project automates the generation of a Clojurescript wrapper which creates namespaces for each component in EUI. It also generates a [components table](./components.md) that maps variable names to their original file on disk, and the Clojurescript namespace that they live in.

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
            [eui.loading-spinner :refer [EuiLoadingSpinner]]
            [eui.text :refer [EuiText]]))
```

We are also able to overwrite certain components without the end-developer's knowledge which keeps the experience unified. For example we needed to port the text field components so they would compatible with Reagent's async-rendering. Those components live in the overrwrites directory.

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

## How to bump the version of EUI and re-generate the library

Steps are currently:

- Update the version of EUI in the `package.json` file
- Run `make generate` from the root of the project
- Update the EUI version in `src/deps.cljs` so projects using this will install the correct version we're using
- Commit and submit a pull request
