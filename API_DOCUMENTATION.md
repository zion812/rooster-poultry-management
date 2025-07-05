# 📚 ROOSTER API DOCUMENTATION

**Complete API Reference for Krishna District Poultry Management System**

## 🎯 **API OVERVIEW**

The Rooster API provides comprehensive endpoints for managing poultry operations in Krishna
District, supporting farmers, buyers, administrators, and veterinarians through a multi-role
platform.

### **Base Configuration**

- **Base URL**: `https://api.rooster-poultry.com/v1`
- **Authentication**: Firebase Auth + Custom JWT
- **Content Type**: `application/json`
- **Rate Limiting**: 1000 requests/hour per user

---

## 🔐 **AUTHENTICATION**

### **Login**

```http
POST /auth/login
Content-Type: application/json

{
  "email": "farmer@example.com",
  "password": "securePassword123",
  "role": "FARMER"
}
```

**Response:**

```json
{
  "success": true,
  "data": {
    "token": "jwt_token_here",
    "user": {
      "id": "user_123",
      "email": "farmer@example.com",
      "role": "FARMER",
      "profile": {
        "name": "రాముడు",
        "phoneNumber": "+91-9876543210",
        "location": "Vijayawada, Krishna District"
      }
    },
    "expiresIn": "24h"
  }
}
```

### **Register**

```http
POST /auth/register
Content-Type: application/json

{
  "email": "newfarmer@example.com",
  "password": "securePassword123",
  "phoneNumber": "+91-9876543210",
  "role": "FARMER",
  "profile": {
    "name": "కృష్ణుడు",
    "location": "Machilipatnam, Krishna District",
    "language": "te"
  }
}
```

---

## 🐓 **USER MANAGEMENT**

### **Get User Profile**

```http
GET /users/profile
Authorization: Bearer {jwt_token}
```

**Response:**

```json
{
  "success": true,
  "data": {
    "id": "user_123",
    "email": "farmer@example.com",
    "role": "FARMER",
    "profile": {
      "name": "రాముడు",
      "phoneNumber": "+91-9876543210",
      "location": "Vijayawada, Krishna District",
      "language": "te",
      "registrationDate": "2024-01-15T10:30:00Z",
      "lastActive": "2024-01-20T15:45:00Z"
    },
    "farmIds": ["farm_456", "farm_789"],
    "preferences": {
      "notifications": true,
      "language": "te",
      "currency": "INR"
    }
  }
}
```

### **Update User Profile**

```http
PUT /users/profile
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "profile": {
    "name": "రాముడు గారు",
    "phoneNumber": "+91-9876543210",
    "location": "Updated Location"
  },
  "preferences": {
    "notifications": true,
    "language": "te"
  }
}
```

---

## 🏡 **FARM MANAGEMENT**

### **Get User Farms**

```http
GET /farms
Authorization: Bearer {jwt_token}
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "id": "farm_456",
      "name": "Krishna Poultry Farm",
      "ownerId": "user_123",
      "location": {
        "address": "Vijayawada, Krishna District",
        "coordinates": {
          "latitude": 16.5062,
          "longitude": 80.6480
        }
      },
      "totalBirds": 5000,
      "flocks": [
        {
          "id": "flock_001",
          "breed": "BROILER",
          "quantity": 2500,
          "age": 35,
          "healthStatus": "HEALTHY",
          "lastVaccination": "2024-01-10T08:00:00Z"
        }
      ],
      "infrastructure": {
        "coops": 10,
        "feedStorage": "20 tons",
        "waterSystem": "Automated"
      }
    }
  ]
}
```

### **Create Farm**

```http
POST /farms
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "name": "New Krishna Farm",
  "location": {
    "address": "Machilipatnam, Krishna District",
    "coordinates": {
      "latitude": 16.1875,
      "longitude": 81.1389
    }
  },
  "infrastructure": {
    "coops": 5,
    "feedStorage": "10 tons",
    "waterSystem": "Manual"
  }
}
```

### **Add Flock**

```http
POST /farms/{farmId}/flocks
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "breed": "BROILER",
  "quantity": 1000,
  "age": 0,
  "source": "Local Hatchery",
  "acquisitionDate": "2024-01-20T00:00:00Z"
}
```

---

## 🛒 **MARKETPLACE**

### **Get Marketplace Listings**

```http
GET /marketplace/listings?category=BIRDS&location=Krishna&page=1&limit=20
Authorization: Bearer {jwt_token}
```

**Response:**

```json
{
  "success": true,
  "data": {
    "listings": [
      {
        "id": "listing_123",
        "sellerId": "user_456",
        "seller": {
          "name": "లక్ష్మి పౌల్ట్రీ",
          "location": "Vijayawada",
          "rating": 4.8
        },
        "product": {
          "type": "BROILER_BIRDS",
          "breed": "BROILER",
          "quantity": 500,
          "age": 42,
          "weight": "2.2 kg average"
        },
        "price": {
          "amount": 180,
          "currency": "INR",
          "unit": "per_kg"
        },
        "location": "Vijayawada, Krishna District",
        "status": "ACTIVE",
        "postedDate": "2024-01-18T12:00:00Z",
        "images": [
          "https://storage.rooster.com/images/listing_123_1.jpg"
        ]
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 5,
      "totalItems": 95,
      "hasNext": true
    }
  }
}
```

### **Create Listing**

```http
POST /marketplace/listings
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "product": {
    "type": "BROILER_BIRDS",
    "breed": "BROILER",
    "quantity": 1000,
    "age": 45,
    "weight": "2.5 kg average",
    "description": "Healthy broiler chickens ready for market"
  },
  "price": {
    "amount": 200,
    "currency": "INR",
    "unit": "per_kg"
  },
  "location": "Machilipatnam, Krishna District",
  "availableFrom": "2024-01-25T00:00:00Z"
}
```

### **Place Order**

```http
POST /marketplace/orders
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "listingId": "listing_123",
  "quantity": 100,
  "deliveryAddress": {
    "address": "Market Yard, Vijayawada",
    "coordinates": {
      "latitude": 16.5062,
      "longitude": 80.6480
    }
  },
  "paymentMethod": "RAZORPAY"
}
```

---

## 🎯 **AUCTION SYSTEM**

### **Get Active Auctions**

```http
GET /auctions?status=ACTIVE&location=Krishna
Authorization: Bearer {jwt_token}
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "id": "auction_789",
      "sellerId": "user_456",
      "seller": {
        "name": "కృష్ణ పౌల్ట్రీ మార్ట్",
        "rating": 4.9
      },
      "product": {
        "type": "BROILER_BIRDS",
        "quantity": 2000,
        "breed": "BROILER",
        "age": 42
      },
      "startingPrice": {
        "amount": 150,
        "currency": "INR",
        "unit": "per_kg"
      },
      "currentPrice": {
        "amount": 175,
        "currency": "INR",
        "unit": "per_kg"
      },
      "highestBidder": {
        "id": "user_999",
        "name": "అనిల్ ట్రేడర్స్"
      },
      "startTime": "2024-01-20T10:00:00Z",
      "endTime": "2024-01-20T18:00:00Z",
      "status": "ACTIVE",
      "totalBids": 15
    }
  ]
}
```

### **Place Bid**

```http
POST /auctions/{auctionId}/bids
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "amount": 180,
  "currency": "INR",
  "quantity": 2000
}
```

### **Create Auction**

```http
POST /auctions
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "product": {
    "type": "LAYER_BIRDS",
    "breed": "WHITE_LEGHORN",
    "quantity": 1500,
    "age": 18
  },
  "startingPrice": {
    "amount": 300,
    "currency": "INR",
    "unit": "per_bird"
  },
  "startTime": "2024-01-22T09:00:00Z",
  "endTime": "2024-01-22T17:00:00Z",
  "location": "Vijayawada, Krishna District"
}
```

---

## 💳 **PAYMENT PROCESSING**

### **Create Payment Order**

```http
POST /payments/orders
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "orderId": "order_123",
  "amount": 18000,
  "currency": "INR",
  "paymentMethod": "RAZORPAY"
}
```

**Response:**

```json
{
  "success": true,
  "data": {
    "paymentOrderId": "payment_order_456",
    "razorpayOrderId": "order_razorpay_789",
    "amount": 18000,
    "currency": "INR",
    "status": "CREATED"
  }
}
```

### **Verify Payment**

```http
POST /payments/verify
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "paymentOrderId": "payment_order_456",
  "razorpayPaymentId": "pay_razorpay_123",
  "razorpaySignature": "signature_hash"
}
```

---

## 👥 **COMMUNITY FEATURES**

### **Get Community Groups**

```http
GET /community/groups?location=Krishna
Authorization: Bearer {jwt_token}
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "id": "group_123",
      "name": "కృష్ణా జిల్లా పౌల్ట్రీ రైతులు",
      "description": "Krishna District poultry farmers community",
      "memberCount": 1250,
      "location": "Krishna District",
      "category": "FARMERS",
      "language": "te",
      "isPublic": true,
      "createdDate": "2023-12-01T00:00:00Z"
    }
  ]
}
```

### **Post in Community**

```http
POST /community/groups/{groupId}/posts
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "title": "బ్రాయిలర్ ధరలు",
  "content": "Today's broiler prices in Vijayawada market",
  "category": "MARKET_UPDATE",
  "language": "te"
}
```

---

## 🏥 **VETERINARY SERVICES**

### **Get Available Vets**

```http
GET /veterinary/vets?location=Krishna&specialization=POULTRY
Authorization: Bearer {jwt_token}
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "id": "vet_123",
      "name": "Dr. రాజేష్ కుమార్",
      "specialization": "POULTRY",
      "qualifications": "BVSc & AH, MVSc",
      "experience": 8,
      "location": "Vijayawada",
      "rating": 4.7,
      "consultationFee": {
        "amount": 500,
        "currency": "INR"
      },
      "languages": ["te", "en"],
      "availability": {
        "days": ["MONDAY", "TUESDAY", "WEDNESDAY", "FRIDAY"],
        "hours": "09:00-17:00"
      }
    }
  ]
}
```

### **Book Consultation**

```http
POST /veterinary/consultations
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "vetId": "vet_123",
  "farmId": "farm_456",
  "appointmentDate": "2024-01-25T10:00:00Z",
  "type": "FARM_VISIT",
  "issue": "Flock health checkup",
  "urgency": "ROUTINE"
}
```

---

## 📊 **ANALYTICS & REPORTS**

### **Get Farm Analytics**

```http
GET /analytics/farms/{farmId}?period=30d
Authorization: Bearer {jwt_token}
```

**Response:**

```json
{
  "success": true,
  "data": {
    "period": "30d",
    "summary": {
      "totalBirds": 5000,
      "mortalityRate": 2.1,
      "feedConversionRatio": 1.8,
      "avgWeight": 2.3,
      "revenue": 450000,
      "expenses": 320000,
      "profit": 130000
    },
    "trends": {
      "weightGain": [
        {"date": "2024-01-01", "avgWeight": 1.8},
        {"date": "2024-01-15", "avgWeight": 2.1},
        {"date": "2024-01-30", "avgWeight": 2.3}
      ],
      "mortality": [
        {"date": "2024-01-01", "count": 5},
        {"date": "2024-01-15", "count": 8},
        {"date": "2024-01-30", "count": 12}
      ]
    }
  }
}
```

### **Get Market Trends**

```http
GET /analytics/market/trends?location=Krishna&product=BROILER&period=7d
Authorization: Bearer {jwt_token}
```

---

## 🔔 **NOTIFICATIONS**

### **Get Notifications**

```http
GET /notifications?page=1&limit=20
Authorization: Bearer {jwt_token}
```

**Response:**

```json
{
  "success": true,
  "data": {
    "notifications": [
      {
        "id": "notif_123",
        "type": "MARKET_UPDATE",
        "title": "Price Alert",
        "message": "Broiler prices increased by 5% in your area",
        "data": {
          "currentPrice": 185,
          "previousPrice": 176
        },
        "read": false,
        "createdAt": "2024-01-20T15:30:00Z"
      }
    ],
    "unreadCount": 5
  }
}
```

### **Mark as Read**

```http
PUT /notifications/{notificationId}/read
Authorization: Bearer {jwt_token}
```

---

## 📱 **MOBILE SPECIFIC ENDPOINTS**

### **Upload Images**

```http
POST /upload/images
Authorization: Bearer {jwt_token}
Content-Type: multipart/form-data

{
  "file": [image_file],
  "type": "FARM_IMAGE",
  "farmId": "farm_456"
}
```

### **Sync Offline Data**

```http
POST /sync/offline
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "data": [
    {
      "type": "FARM_RECORD",
      "action": "CREATE",
      "timestamp": "2024-01-20T14:30:00Z",
      "data": {...}
    }
  ]
}
```

---

## 🚨 **ERROR HANDLING**

### **Standard Error Response**

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": {
      "field": "email",
      "issue": "Invalid email format"
    },
    "timestamp": "2024-01-20T15:45:00Z"
  }
}
```

### **Common Error Codes**

- `AUTHENTICATION_FAILED` - Invalid credentials
- `AUTHORIZATION_DENIED` - Insufficient permissions
- `VALIDATION_ERROR` - Invalid input data
- `RESOURCE_NOT_FOUND` - Requested resource doesn't exist
- `RATE_LIMIT_EXCEEDED` - Too many requests
- `SERVER_ERROR` - Internal server error

---

## 📋 **API TESTING**

### **Postman Collection**

```bash
# Import Rooster API collection
curl -o rooster-api.postman_collection.json \
  https://api.rooster-poultry.com/docs/postman-collection
```

### **Test Data**

```json
{
  "testUsers": {
    "farmer": {
      "email": "test.farmer@rooster.com",
      "password": "TestPass123"
    },
    "buyer": {
      "email": "test.buyer@rooster.com", 
      "password": "TestPass123"
    }
  }
}
```

---

**📚 ROOSTER API DOCUMENTATION**
**Complete API Reference for Krishna District Poultry Management**

*Empowering poultry farmers through comprehensive digital solutions.*