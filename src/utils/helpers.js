// Placeholder for helper functions
export const formatDate = (date) => {
  // Basic date formatting
  return new Date(date).toLocaleDateString();
};

export const truncateText = (text, length) => {
  if (text.length <= length) {
    return text;
  }
  return text.substring(0, length) + '...';
};
