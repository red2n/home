# Financial Services API Usage Examples

This document provides examples of how to use the Phase 2 Financial Operations APIs.

## Folio Service (Port 8083)

### Create a Folio
```bash
curl -X POST http://localhost:8083/api/folios \
  -H "Content-Type: application/json" \
  -d '{
    "guestId": 1,
    "reservationId": 1,
    "initialAmount": 0.00
  }'
```

### Add Charge to Folio
```bash
curl -X POST http://localhost:8083/api/folios/charges \
  -H "Content-Type: application/json" \
  -d '{
    "folioId": 1,
    "description": "Room Charge",
    "amount": 150.00,
    "chargeType": "ROOM_CHARGE",
    "reference": "Room 101"
  }'
```

### Get Folio by ID
```bash
curl -X GET http://localhost:8083/api/folios/1
```

## Payment Service (Port 8084)

### Process Payment
```bash
curl -X POST http://localhost:8084/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "folioId": 1,
    "amount": 150.00,
    "paymentMethod": "CREDIT_CARD",
    "cardLast4": "1234",
    "cardBrand": "VISA"
  }'
```

### Process Refund
```bash
curl -X POST http://localhost:8084/api/payments/refunds \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": 1,
    "amount": 50.00,
    "reason": "Service adjustment"
  }'
```

### Get Payments for Folio
```bash
curl -X GET http://localhost:8084/api/payments/folio/1
```

## Ledger Service (Port 8085)

### Create Account
```bash
curl -X POST http://localhost:8085/api/ledger/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountName": "Cash Account",
    "accountType": "ASSET",
    "description": "Main cash account"
  }'
```

### Create Journal Entry
```bash
curl -X POST http://localhost:8085/api/ledger/journal-entries \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Room revenue recognition",
    "reference": "FOL-12345",
    "ledgerEntries": [
      {
        "accountId": 1,
        "description": "Cash receipt",
        "debitAmount": 150.00,
        "creditAmount": 0.00
      },
      {
        "accountId": 2,
        "description": "Room revenue",
        "debitAmount": 0.00,
        "creditAmount": 150.00
      }
    ]
  }'
```

### Post Journal Entry
```bash
curl -X PUT http://localhost:8085/api/ledger/journal-entries/1/post
```

### Get Account Balance
```bash
curl -X GET http://localhost:8085/api/ledger/accounts/1/balance
```

## Financial Workflow Example

1. **Create Folio**: Guest checks in, folio is created
2. **Add Charges**: Room charges, taxes, and services are posted
3. **Process Payment**: Guest pays with credit card
4. **Record Transaction**: Double-entry accounting records are created
5. **Verify Balances**: Account balances are updated correctly

This workflow ensures proper financial tracking and audit trails for the Property Management System.