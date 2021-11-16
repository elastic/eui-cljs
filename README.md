# A Clojurescript wrapper for EUI v[41.0.0](https://elastic.github.io/eui/#/package/changelog)

Smooths out the experience of using EUI from Clojurescript.

[![Clojars Project](https://img.shields.io/clojars/v/co.elastic/eui-cljs.svg)](https://clojars.org/co.elastic/eui-cljs)

## Quickstart

* Add `elastic/eui-cljs` to your project
* Add the [Cheshire](https://github.com/dakrone/cheshire) dependency to your project
  - Our theme macro uses it to generate an EDN map of theme values, won't increase bundle size
* Call `(eui.theme/init!)` in your application's main function

Check out the sample application in the `examples` directory to get a feel for how to use it, and then head over to the [official EUI docs site](https://eui.elastic.co/) to start learning!

When in doubt, view the [components table](./components.md) to see exactly where we are mapping variables you come across.

## Motivation

It's currently difficult to track down exactly which file any given EUI variable lives in. Every component should be imported separately because that reduces the final bundle size.

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

When importing directly from `node_modules/@elastic/eui`, the import section looks like:

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
```

We are also able to overwrite certain components without the end-developer's knowledge which keeps the experience unified. For example we needed to port the text field components so they would compatible with Reagent's async-rendering. Those components live in the overrwrites directory.

## Styling/Theming

We have macros which load in CSS and JSON and they use [Cheshire JSON](https://github.com/dakrone/cheshire) So you'll need to add that dependency to your project. It won't increase the final bundle size. 

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

## How to bump the version of EUI and re-generate the library

Steps are currently:

### Generate a candidate -SNAPSHOT release
- Update the version of EUI in the `package.json` file
- Update the EUI version in `src/deps.cljs` so projects using this will install the correct version we're using
- Run `make generate` from the root of the project
- Commit and submit a pull request
- Run `make release` to deploy the -SNAPSHOT version
- Test to verify all is okay

### Promote to actual version
- Remove -SNAPSHOT from `release.edn`
- Run `make release`

### Deploy to Clojars

Make sure `CLOJARS_USERNAME` and `CLOJARS_PASSWORD` environment variables are set
(unless you are passing in `--clojars-username` and `--clojars-password` directly).
For example, add the following to your `~/.bashrc` or `~/.zshrc` or equivalent:

```sh
export CLOJARS_USERNAME="XYZ"
export CLOJARS_PASSWORD="XYZ"
```

Create an initial version tag (if you haven't already)

```sh
git tag v0.1.0
```

Release a new version (tag + pom + jar + deploy):

```sh
make release
```

## License

[Dual-licensed under Elastic v2 and Server Side Public License, v 1][license] Read the [FAQ][faq] for details.

[license]: LICENSE.txt
[faq]: https://github.com/elastic/eui/blob/main/FAQ.md
