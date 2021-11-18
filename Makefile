.PHONY: repl
repl:
	clojure -M:scripts:repl

.PHONY: generate
generate:
	git checkout main
	clojure -M:scripts:update-deps $(VERSION)
	yarn
	rm -rf src/eui
	clojure -M:scripts:generate
	git checkout -b "v${VERSION}-RC"
	git add .
	git commit -am "EUI v${VERSION} Release candidate"
	git push origin "v${VERSION}-RC"

.PHONY: clean
clean:
	rm -rf node_modules
	rm -rf .cpcache

.PHONY: release
release:
	git checkout -b "v${VERSION}"
	git commit -am "EUI v${VERSION} Release"
	git push origin "v${VERSION}"
	clj -M:release
	git push --tags
