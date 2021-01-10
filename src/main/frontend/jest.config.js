// @todo #1484 Document Jest usage
module.exports = {
	// https://jestjs.io/docs/en/configuration#watchman-boolean
	"watchman": false,
	// required to use ECMAScript Modules (ESM)
	// @todo #1484 Find a better way to use ESM with Jest and replace jest-esm-transformer
	"transform": {
		"\\.js$": "jest-esm-transformer"
	},
	// See: https://github.com/facebook/jest/issues/5064#issuecomment-401451361
	// @todo #1484 Remove usage of jest-standard-reporter once facebook/jest#5064 is resolved
	"reporters": [ "jest-standard-reporter" ]
}
