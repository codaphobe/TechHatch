export const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
};

export const formatDateRelative = (dateString) => {
  const date = new Date(dateString);
  const now = new Date();
  const diffTime = Math.abs(now - date);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays === 0) return 'Today';
  if (diffDays === 1) return 'Yesterday';
  if (diffDays < 7) return `${diffDays} days ago`;
  if (diffDays < 30) return `${Math.floor(diffDays / 7)} weeks ago`;
  if (diffDays < 365) return `${Math.floor(diffDays / 30)} months ago`;
  return `${Math.floor(diffDays / 365)} years ago`;
};

export const formatSalary = (min, max, currency = 'INR') => {
  const formatAmount = (amount) => {
    if (amount >= 100000) {
      return `${(amount / 100000).toFixed(1)} LPA`;
    }
    return `${amount.toLocaleString('en-IN')}`;
  };

  if (min && max) {
    return `${formatAmount(min)} - ${formatAmount(max)} ${currency}`;
  }
  if (min) {
    return `${formatAmount(min)}+ ${currency}`;
  }
  return 'Not disclosed';
};
