from datetime import datetime
from enum import Enum

class DiseaseSymptoms(Enum):
    RESPIRATORY_DISTRESS = "Respiratory Distress"
    DIARRHEA = "Diarrhea"
    LETHARGY = "Lethargy"
    REDUCED_APPETITE = "Reduced Appetite"
    FEATHER_LOSS = "Feather Loss"
    SWELLING = "Swelling"
    NASAL_DISCHARGE = "Nasal Discharge"
    OTHER = "Other"

class RecordType(Enum):
    DISEASE_INCIDENT = "Disease Incident"
    VACCINATION = "Vaccination"
    MORTALITY = "Mortality"
    GENERAL_CHECKUP = "General Checkup"

class HealthRecord:
    def __init__(self, record_id: str, flock_id: str, record_date: datetime, record_type: RecordType,
                 details: str, veterinarian: str = "", cost: float = 0.0):
        self.record_id = record_id # Unique ID for the health record
        self.flock_id = flock_id # ID of the flock this record pertains to
        self.record_date = record_date
        self.record_type = record_type
        self.details = details # General description or notes
        self.veterinarian = veterinarian
        self.cost = cost # Cost associated with this health event (e.g., vet visit, medicine)

    def __repr__(self):
        return f"<HealthRecord {self.record_id} (Flock: {self.flock_id}, Type: {self.record_type.value})>"

    def to_dict(self):
        return {
            "record_id": self.record_id,
            "flock_id": self.flock_id,
            "record_date": self.record_date.isoformat() if self.record_date else None,
            "record_type": self.record_type.value,
            "details": self.details,
            "veterinarian": self.veterinarian,
            "cost": self.cost
        }

class DiseaseIncidentRecord(HealthRecord):
    def __init__(self, record_id: str, flock_id: str, record_date: datetime, details: str,
                 disease_name: str, symptoms: list[DiseaseSymptoms], treatment_administered: str = "",
                 affected_count: int = 0, veterinarian: str = "", cost: float = 0.0):
        super().__init__(record_id, flock_id, record_date, RecordType.DISEASE_INCIDENT, details, veterinarian, cost)
        self.disease_name = disease_name
        self.symptoms = symptoms # List of DiseaseSymptoms enums
        self.treatment_administered = treatment_administered
        self.affected_count = affected_count

    def to_dict(self):
        data = super().to_dict()
        data.update({
            "disease_name": self.disease_name,
            "symptoms": [symptom.value for symptom in self.symptoms],
            "treatment_administered": self.treatment_administered,
            "affected_count": self.affected_count
        })
        return data

class VaccinationRecord(HealthRecord):
    def __init__(self, record_id: str, flock_id: str, record_date: datetime, details: str,
                 vaccine_name: str, administered_by: str, dosage: str = "",
                 vaccinated_count: int = 0, veterinarian: str = "", cost: float = 0.0):
        super().__init__(record_id, flock_id, record_date, RecordType.VACCINATION, details, veterinarian, cost)
        self.vaccine_name = vaccine_name
        self.administered_by = administered_by
        self.dosage = dosage
        self.vaccinated_count = vaccinated_count

    def to_dict(self):
        data = super().to_dict()
        data.update({
            "vaccine_name": self.vaccine_name,
            "administered_by": self.administered_by,
            "dosage": self.dosage,
            "vaccinated_count": self.vaccinated_count
        })
        return data

class MortalityRecord(HealthRecord):
    def __init__(self, record_id: str, flock_id: str, record_date: datetime, details: str,
                 cause_of_death: str, number_of_deaths: int, post_mortem_findings: str = "",
                 veterinarian: str = "", cost: float = 0.0): # Cost might be for disposal or examination
        super().__init__(record_id, flock_id, record_date, RecordType.MORTALITY, details, veterinarian, cost)
        self.cause_of_death = cause_of_death
        self.number_of_deaths = number_of_deaths
        self.post_mortem_findings = post_mortem_findings

    def to_dict(self):
        data = super().to_dict()
        data.update({
            "cause_of_death": self.cause_of_death,
            "number_of_deaths": self.number_of_deaths,
            "post_mortem_findings": self.post_mortem_findings
        })
        return data
