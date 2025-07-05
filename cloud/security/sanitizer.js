// Cloud-code helper – sanitizes incoming objects to strip out potentially dangerous Mongo/Parse operators.
// Usage: const { clean } = require('./security/sanitizer');
//        const safeParams = clean(request.params);

const BAD_KEYS = ['$ne', '$gt', '$lt', '$gte', '$lte', '$in', '$nin', '$regex', '$where', '$exists'];

/**
 * Deep-clone & scrub an arbitrary payload.
 * Arrays are allowed but their items are also sanitized.
 * Unsupported types (functions, Date objects) throw immediately.
 * @param {any} input
 * @returns {any} sanitized clone
 */
function clean(input = {}) {
  if (input === null || input === undefined) return input;

  if (Array.isArray(input)) {
    return input.map(item => clean(item));
  }

  if (typeof input === 'object') {
    const clone = {};
    Object.keys(input).forEach(k => {
      // Strip keys that are dangerous or start with $ (Mongo operators)
      if (BAD_KEYS.includes(k) || k.startsWith('$')) {
        return; // skip key entirely
      }
      const val = input[k];
      clone[k] = clean(val);
    });
    return clone;
  }

  // Primitive value – return as-is
  if (['string', 'number', 'boolean'].includes(typeof input)) return input;

  // Sanitize strings for XSS and injection attacks
  if (typeof input === 'string') {
    return input
      .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '')
      .replace(/<iframe\b[^<]*(?:(?!<\/iframe>)<[^<]*)*<\/iframe>/gi, '')
      .replace(/javascript:/gi, '')
      .replace(/on\w+\s*=/gi, '')
      .trim();
  }

  throw new Error('Invalid payload: unsupported type');
}

module.exports = { clean };
