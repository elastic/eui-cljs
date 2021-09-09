.PHONY: repl
repl:
	clojure -M:bin:repl

.PHONY: generate
generate:
	yarn
	rm -rf src/eui
	clojure -M:bin:generate

.PHONY: clean
clean:
	rm -rf node_modules
	rm -rf .cpcache

.PHONY: release
release:
	clj -M:release
