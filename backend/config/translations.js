/**
 * Translations for English and Telugu languages
 * Optimized for rural farmer understanding
 */

const translations = {
  en: {
    predictedPrice: 'Predicted price: ₹{price}',
    error: 'Error: {message}',
    unauthorized: 'Unauthorized access',
    regionRequired: 'Region is required',
    noDataFound: 'No price data found for this region',
    invalidRegion: 'Invalid region specified',
    serverError: 'Server error occurred',
    rateLimitExceeded: 'Too many requests. Please try again later.',
    success: 'Success',
    priceRange: 'Price range: ₹{min} - ₹{max}',
    marketTrend: 'Market trend: {trend}',
    lastUpdated: 'Last updated: {date}',
    confidence: 'Prediction confidence: {level}%',
    recommendation: 'Recommendation: {advice}'
  },
  te: {
    predictedPrice: 'అంచనా ధర: ₹{price}',
    error: 'లోపం: {message}',
    unauthorized: 'అనధికార ప్రవేశం',
    regionRequired: 'ప్రాంతం అవసరం',
    noDataFound: 'ఈ ప్రాంతానికి ధర డేటా లేదు',
    invalidRegion: 'చెల్లని ప్రాంతం పేర్కొనబడింది',
    serverError: 'సర్వర్ లోపం సంభవించింది',
    rateLimitExceeded: 'చాలా అభ్యర్థనలు. దయచేసి తరువాత మళ్లీ ప్రయత్నించండి.',
    success: 'విజయవంతం',
    priceRange: 'ధర పరిధి: ₹{min} - ₹{max}',
    marketTrend: 'మార్కెట్ ట్రెండ్: {trend}',
    lastUpdated: 'చివరిగా నవీకరించబడింది: {date}',
    confidence: 'అంచనా విశ్వసనీయత: {level}%',
    recommendation: 'సిఫార్సు: {advice}'
  }
};

/**
 * Get translated message with parameter substitution
 * @param {string} lang - Language code (en/te)
 * @param {string} key - Translation key
 * @param {object} params - Parameters to substitute
 * @returns {string} Translated message
 */
function getMessage(lang = 'en', key, params = {}) {
  const language = translations[lang] || translations.en;
  let message = language[key] || translations.en[key] || key;
  
  // Replace parameters in the message
  Object.keys(params).forEach(param => {
    const placeholder = `{${param}}`;
    message = message.replace(new RegExp(placeholder, 'g'), params[param]);
  });
  
  return message;
}

/**
 * Format price for display (handles Indian number formatting)
 * @param {number} price - Price value
 * @param {string} lang - Language code
 * @returns {string} Formatted price
 */
function formatPrice(price, lang = 'en') {
  if (!price || isNaN(price)) return '0';
  
  // Indian number formatting (lakhs, crores)
  const formatter = new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  });
  
  return formatter.format(price).replace('₹', '');
}

/**
 * Get trend description in the specified language
 * @param {string} trend - Trend value (up/down/stable)
 * @param {string} lang - Language code
 * @returns {string} Trend description
 */
function getTrendDescription(trend, lang = 'en') {
  const trends = {
    en: {
      up: 'Rising ↗️',
      down: 'Falling ↘️',
      stable: 'Stable ➡️'
    },
    te: {
      up: 'పెరుగుతున్న ↗️',
      down: 'తగ్గుతున్న ↘️',
      stable: 'స్థిరమైన ➡️'
    }
  };
  
  const language = trends[lang] || trends.en;
  return language[trend] || trend;
}

module.exports = {
  translations,
  getMessage,
  formatPrice,
  getTrendDescription
};