.PHONY: repl
repl:
	clojure -A:bin:repl

.PHONY: generate
generate:
	yarn
	rm -rf src/eui
	clojure -A:bin:generate

.PHONY: clean
clean:
	rm -rf node_modules
	rm -rf .cpcache

.PHONY: release
release: generate
	clj -A:release
