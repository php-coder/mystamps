const path = require('path');
const webpack = require('webpack');

const SRC_DIR = './src/components/';

// @todo #1455 Remove export of components to window
module.exports = {
    mode: 'production',
    entry: {
        AddCatalogNumbersForm: SRC_DIR + 'AddCatalogNumbersForm.js',
        AddCatalogPriceForm:   SRC_DIR + 'AddCatalogPriceForm.js',
        AddCommentForm:        SRC_DIR + 'AddCommentForm.js',
        AddReleaseYearForm:    SRC_DIR + 'AddReleaseYearForm.js',
        HideImageForm:         SRC_DIR + 'HideImageForm.js',
        SeriesSaleImportForm:  SRC_DIR + 'SeriesSaleImportForm.js',
        SeriesSalesList:       SRC_DIR + 'SeriesSalesList.js',
        SimilarSeriesForm:     SRC_DIR + 'SimilarSeriesForm.js'
    },
    output: {
        path: path.resolve(__dirname, '../../../target/classes/js/components'),
        filename: '[name].min.js'
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: {
                    // See also the configuration in babel.config.js
                    loader: 'babel-loader'
                }
            }
        ]
    },
    externals: {
        // Don't include React library to bundles even if the code has import-s.
        // We need these import-s for unit tests but we load React separately on the pages.
        // See https://v4.webpack.js.org/configuration/externals/
        'react': 'React'
    }
}
