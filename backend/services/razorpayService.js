const Razorpay = require('razorpay');
const crypto = require('crypto');

class RazorpayService {
  constructor() {
    if (!process.env.RAZORPAY_KEY_ID || !process.env.RAZORPAY_KEY_SECRET) {
      console.error('❌ Razorpay Key ID or Key Secret not configured in environment variables.');
      // In a real app, you might throw an error here or handle it more gracefully
      // For now, allow constructor to proceed but operations will fail.
      this.instance = null;
    } else {
      this.instance = new Razorpay({
        key_id: process.env.RAZORPAY_KEY_ID,
        key_secret: process.env.RAZORPAY_KEY_SECRET,
      });
      console.log('💳 Razorpay Service Initialized');
    }
  }

  /**
   * Creates a Razorpay order.
   * @param {number} amount - Amount in the smallest currency unit (e.g., paise for INR).
   * @param {string} currency - Currency code (e.g., 'INR').
   * @param {string} receiptId - A unique receipt identifier from your system.
   * @param {object} [notes={}] - Custom notes to associate with the order.
   * @returns {Promise<object>} The Razorpay order object.
   * @throws {Error} If order creation fails.
   */
  async createOrder(amount, currency, receiptId, notes = {}) {
    if (!this.instance) {
      throw new Error('Razorpay service not initialized. Check API keys.');
    }

    const options = {
      amount: Math.round(amount), // Ensure amount is an integer
      currency,
      receipt: receiptId,
      notes,
    };

    try {
      const order = await this.instance.orders.create(options);
      console.log(`🛍️ Razorpay order created: ${order.id} for receipt: ${receiptId}`);
      return order;
    } catch (error) {
      console.error(`❌ Error creating Razorpay order for receipt ${receiptId}:`, error);
      throw new Error(`Failed to create Razorpay order: ${error.error?.description || error.message}`);
    }
  }

  /**
   * Verifies a Razorpay payment signature.
   * @param {string} razorpayOrderId - The ID of the Razorpay order.
   * @param {string} razorpayPaymentId - The ID of the Razorpay payment.
   * @param {string} razorpaySignature - The signature received from Razorpay.
   * @returns {boolean} True if the signature is valid, false otherwise.
   */
  verifyPaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature) {
    if (!process.env.RAZORPAY_KEY_SECRET) {
        console.error('❌ Razorpay Key Secret not configured. Cannot verify signature.');
        return false;
    }
    if (!razorpayOrderId || !razorpayPaymentId || !razorpaySignature) {
        console.error('❌ Missing parameters for signature verification.');
        return false;
    }

    const body = razorpayOrderId + "|" + razorpayPaymentId;
    const expectedSignature = crypto
      .createHmac('sha256', process.env.RAZORPAY_KEY_SECRET)
      .update(body.toString())
      .digest('hex');

    if (expectedSignature === razorpaySignature) {
      console.log(`✅ Razorpay signature verified for order: ${razorpayOrderId}`);
      return true;
    } else {
      console.warn(`⚠️ Razorpay signature verification failed for order: ${razorpayOrderId}. Expected: ${expectedSignature}, Got: ${razorpaySignature}`);
      return false;
    }
  }

  /**
   * Verifies a Razorpay webhook signature.
   * @param {string} rawBody - The raw request body from the webhook.
   * @param {string} signature - The value of the 'X-Razorpay-Signature' header.
   * @param {string} webhookSecret - Your Razorpay webhook secret configured in the dashboard.
   * @returns {boolean} True if the signature is valid, false otherwise.
   */
  verifyWebhookSignature(rawBody, signature, webhookSecret) {
    if (!webhookSecret) {
        console.error('❌ Razorpay Webhook Secret not configured. Cannot verify webhook signature.');
        return false;
    }
     if (!rawBody || !signature) {
        console.error('❌ Missing parameters for webhook signature verification.');
        return false;
    }

    try {
        const expectedSignature = crypto
            .createHmac('sha256', webhookSecret)
            .update(rawBody)
            .digest('hex');

        if (expectedSignature === signature) {
            console.log(`✅ Razorpay webhook signature verified.`);
            return true;
        } else {
            console.warn(`⚠️ Razorpay webhook signature verification failed. Expected: ${expectedSignature}, Got: ${signature}`);
            return false;
        }
    } catch (error) {
        console.error('❌ Error verifying webhook signature:', error);
        return false;
    }
  }
}

module.exports = RazorpayService;
