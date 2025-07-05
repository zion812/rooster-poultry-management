# This file makes Python treat the `world_bank` directory as a package.

# Import fetcher classes to make them easily accessible
from .world_bank_data import (
    DeathRateFetcher,
    NeonatalMortalityFetcher,
    InfantMortalityFetcher,
    Under5MortalityFetcher,
    WorldBankDataFetcher  # Also exposing the base if needed elsewhere
)

# List of available fetcher classes for automated processing
FETCHER_CLASSES = [
    DeathRateFetcher,
    NeonatalMortalityFetcher,
    InfantMortalityFetcher,
    Under5MortalityFetcher,
]

__all__ = [
    'WorldBankDataFetcher',
    'DeathRateFetcher',
    'NeonatalMortalityFetcher',
    'InfantMortalityFetcher',
    'Under5MortalityFetcher',
    'FETCHER_CLASSES'
]
