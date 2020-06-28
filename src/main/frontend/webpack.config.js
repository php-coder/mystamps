const path = require('path');

const SRC_DIR = './src/components/';

// @todo #1455 Remove export of components to window
module.exports = {
    mode: 'production',
    entry: {
        AddCatalogNumbersForm: SRC_DIR + 'AddCatalogNumbersForm.js',
        AddCatalogPriceForm:   SRC_DIR + 'AddCatalogPriceForm.js',
        AddCommentForm:        SRC_DIR + 'AddCommentForm.js',
        AddReleaseYearForm:    SRC_DIR + 'AddReleaseYearForm.js',
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
                    loader: 'babel-loader',
                    options: {
                        presets: [ "@babel/preset-react", "@babel/preset-env" ]
                    }
                }
            }
        ]
    }
}
