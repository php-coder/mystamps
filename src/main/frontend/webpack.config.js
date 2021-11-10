const path = require('path');
const webpack = require('webpack');

// @todo #1455 Remove export of components to window
module.exports = {
    mode: 'production',
    entry: {
        'utils/DateUtils':                  './src/utils/DateUtils.js',

        'components/AddCatalogNumbersForm': './src/components/AddCatalogNumbersForm.js',
        'components/AddCatalogPriceForm':   './src/components/AddCatalogPriceForm.js',
        'components/AddCommentForm':        './src/components/AddCommentForm.js',
        'components/AddReleaseYearForm':    './src/components/AddReleaseYearForm.js',
        'components/HideImageForm':         './src/components/HideImageForm.js',
        'components/SeriesSaleImportForm':  './src/components/SeriesSaleImportForm.js',
        'components/SeriesSalesList':       './src/components/SeriesSalesList.js',
        'components/SimilarSeriesForm':     './src/components/SimilarSeriesForm.js'
    },
    output: {
        // As we can't have a dynamic output path, we've included a subdirectory in entry names. See:
        // https://stackoverflow.com/questions/35903246/how-to-create-multiple-output-paths-in-webpack-config
        path: path.resolve(__dirname, '../../../target/classes/js'),
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
