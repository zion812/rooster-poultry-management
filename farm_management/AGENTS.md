## Agent Instructions for Farm Management System Development

This document provides guidance for AI agents working on the Farm Management System project.

### 1. Coding Style and Conventions:
- Follow PEP 8 for Python code.
- Use type hinting for all function signatures and complex variable declarations.
- Model classes should have a `to_dict()` method for serialization and a `__repr__()` for debugging.
- Ensure IDs for models (Farm, Flock, Records) are treated as strings and are designed to be unique. UUIDs are a good choice for generation if not provided.
- Dates and datetimes should use the `datetime` module from the Python standard library. Ensure consistent timezone handling if applicable (though for local farm management, naive datetimes might suffice initially).

### 2. Modularity:
- **Models (`models/`):** Plain data classes. Business logic directly related to the model's data (like calculated properties) can be included.
- **Repositories (`repositories/`):** Handle data storage and retrieval logic (CRUD operations). They should abstract the actual data source (e.g., database, file system, in-memory store). Initially, we might use in-memory storage for rapid prototyping.
- **Services (`services/`):** Contain business logic that orchestrates operations between models and repositories, or implements more complex features (e.g., alert systems, analytics).
- **UI (`ui/`):** Contains code related to the user interface. The specific structure will depend on the chosen UI framework (e.g., Flask templates, Kivy widgets, etc.). For now, this might be command-line interfaces or simple web interfaces.
- **Utils (`utils/`):** Helper functions, constants, or common utilities used across the project.

### 3. User Experience (UX) Focus:
- Target users are farmers in Krishna District, potentially with limited technical experience.
- Interfaces should be simple, intuitive, and require minimal training.
- Use clear language, avoiding technical jargon where possible.
- Offline capability is crucial. Design data storage and synchronization with this in mind.

### 4. Error Handling:
- Implement robust error handling.
- Provide clear feedback to the user in case of errors.
- Repositories should handle data validation before persistence.

### 5. Offline First:
- The system must be designed to work offline.
- Data should be stored locally first.
- Synchronization with a central server (if any) should happen when network connectivity is available.
- Consider potential data conflicts during synchronization and how to resolve them (e.g., last write wins, or manual resolution).

### 6. Data Model Specifics:
- **Family Tree:** For `Flock` models, the `parent_flock_id_male` and `parent_flock_id_female` are crucial for traceability. The repository handling flocks should have methods to manage and query these relationships.
- **IDs:** Ensure a consistent strategy for generating unique IDs (e.g., `farm-XYZ`, `flock-ABC-123`, `record-TIMESTAMP-UUID`). `uuid.uuid4()` can be useful.

### 7. Iterative Development:
- Follow the plan steps.
- Build core functionalities first, then layer advanced features on top.
- Regularly test implemented features.

### 8. Dependencies:
- Keep `requirements.txt` updated with any new libraries added.
- Prefer well-maintained and widely used libraries.

### 9. Testing:
- Write unit tests for repository methods and critical business logic in services.
- As UI elements are developed, plan for UI testing (manual or automated).

### 10. Communication:
- If any part of the plan or requirements is unclear, ask for clarification.
- Provide updates on progress and any challenges encountered.

By following these guidelines, we aim to build a robust, user-friendly, and effective farm management system.

### 11. IoT Device Integration (Conceptual)

Integrating with IoT devices can automate data collection, providing more accurate and timely information. This section outlines conceptual considerations.

**A. Potential Data Points for Automation:**

*   **Environmental Monitoring (per shed/farm):**
    *   Temperature: Continuous monitoring.
    *   Humidity: Continuous monitoring.
    *   Air Quality (CO2, Ammonia levels): Important for bird health.
    *   Light levels/duration.
    *   *Relevant Model (Conceptual):* A new `EnvironmentRecord` model could be introduced, or these fields could be added to existing daily farm/flock logs.
*   **Resource Consumption:**
    *   Feed Consumption: Sensors on feed silos (weight), auger-based feeder counters.
        *   *Relevant Model:* `FeedConsumptionRecord` could be auto-generated or augmented.
    *   Water Consumption: Water flow meters.
        *   *Relevant Model (Conceptual):* New `WaterConsumptionRecord`.
*   **Production Monitoring:**
    *   Egg Counting: Sensors on conveyor belts in layer operations.
        *   *Relevant Model:* `ProductionRecord` (egg counts).
*   **Bird Health & Behavior (Advanced):**
    *   Bird Weight: Automated weighing scales birds might walk over.
        *   *Relevant Model:* `GrowthRecord`.
    *   Activity Levels: Accelerometers or image analysis (complex).
    *   Sound Analysis: Detecting stress or disease calls (complex).

**B. Data Formats and Communication Protocols:**

*   **Data Format:**
    *   **JSON:** Widely used, human-readable, easy to parse. Preferred for most application-level data exchange.
    *   **Protocol Buffers (protobuf) / MessagePack:** More compact binary formats, efficient for high-volume or constrained devices.
*   **Communication Protocols:**
    *   **MQTT (Message Queuing Telemetry Transport):**
        *   Lightweight, publish-subscribe model.
        *   Efficient for sending data from many devices.
        *   Handles intermittent connectivity well.
        *   Requires an MQTT Broker (e.g., Mosquitto, cloud-based MQTT services).
        *   *Typical Flow:* Device -> MQTT Broker -> Application Backend Service (subscribes to topics).
    *   **HTTP/HTTPS (REST APIs):**
        *   Devices send data via POST requests to a server endpoint.
        *   Simpler to implement for basic scenarios if devices have reliable IP connectivity.
        *   Can be less efficient than MQTT for high-frequency data from many devices.
        *   *Typical Flow:* Device -> HTTP/HTTPS API Endpoint on Application Backend.
    *   **CoAP (Constrained Application Protocol):**
        *   Designed for constrained devices and networks (similar to HTTP but UDP-based).
    *   **LPWAN (Low-Power Wide-Area Networks):**
        *   **LoRaWAN, Sigfox, NB-IoT:** For devices that need long range and low power consumption, sending small amounts of data.
        *   These typically involve specific gateways and network servers provided by the LPWAN operator.
        *   *Typical Flow:* Device -> LPWAN Gateway -> LPWAN Network Server -> Application Backend (via integration like HTTP callback or MQTT).

**C. Conceptual System Architecture for IoT Data:**

1.  **IoT Devices/Sensors:** Deployed on the farm (e.g., temperature sensor, feed scale).
2.  **Gateway (Optional):** Some devices (especially those using short-range wireless like Zigbee, Bluetooth) might connect to a local gateway. The gateway then aggregates data and forwards it using IP-based protocols (MQTT/HTTP). LPWAN devices communicate with specialized LPWAN gateways.
3.  **Network Layer:**
    *   Internet connectivity (Wi-Fi, Ethernet, Cellular).
    *   MQTT Broker or HTTP/S API Server (this would be part of the application's backend, not the CLI itself).
4.  **Application Backend Service:**
    *   A server-side application (not the CLI) responsible for:
        *   Authenticating devices.
        *   Receiving and parsing data from devices/gateways.
        *   Validating data.
        *   Storing data in the central database (which the CLI's repositories would then also interact with, if this CLI were part of a larger system).
        *   Potentially processing data for real-time alerts or analytics.
5.  **Data Storage:** The central database where IoT data is stored alongside manually entered data.
6.  **CLI Application Interaction (Conceptual):**
    *   If this CLI application were to use IoT data, it would typically fetch it from the repositories, which in turn get it from the central database that was populated by the backend service.
    *   Direct communication from IoT devices to this CLI application is generally not a scalable or robust architecture for real-time data. The CLI is a user interface, not a data ingestion service.

**D. Implications for Current Models:**

*   **Timestamps:** IoT data would inherently have precise timestamps. Our models should ensure `record_date` can store `datetime` with sufficient precision.
*   **Source Field:** Adding a `source` field to records (e.g., 'manual', 'iot_sensor_xyz') could be useful for distinguishing data origins.
*   **Data Volume:** Automated collection can generate much more data, impacting storage and query performance strategies for a persistent database.

This conceptual outline provides a basis for future expansion towards IoT integration. The current CLI focuses on manual data entry but is designed with models that could accommodate automated inputs.
