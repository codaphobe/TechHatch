import { STATUS_COLORS, APPLICATION_STATUS } from '../../utils/constants';

const StatusBadge = ({ status }) => {
  const colorClass = STATUS_COLORS[status] || 'bg-gray-500';
  const label = APPLICATION_STATUS[status] || status;

  return (
    <span className={`inline-block rounded-full px-3 py-1 text-xs font-semibold text-white ${colorClass}`}>
      {label}
    </span>
  );
};

export default StatusBadge;
