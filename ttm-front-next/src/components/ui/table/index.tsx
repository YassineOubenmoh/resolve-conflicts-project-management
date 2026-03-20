import React, { ReactNode } from "react";

// General Table Props
interface TableProps {
  children: ReactNode;
  className?: string;
}
interface TableHeaderProps {
  children: ReactNode;
  className?: string;
}
interface TableBodyProps {
  children: ReactNode;
  className?: string;
}
interface TableRowProps {
  children: ReactNode;
  className?: string;
}

// Unified TableCell Props for both <td> and <th>
interface TableCellProps extends React.HTMLAttributes<HTMLTableCellElement> {
  children: ReactNode;
  isHeader?: boolean;
  colSpan?: number;
  rowSpan?: number;
}

// Table Component
const Table: React.FC<TableProps> = ({ children, className }) => {
  return <table className={`min-w-full ${className}`}>{children}</table>;
};

const TableHeader: React.FC<TableHeaderProps> = ({ children, className }) => {
  return <thead className={className}>{children}</thead>;
};

const TableBody: React.FC<TableBodyProps> = ({ children, className }) => {
  return <tbody className={className}>{children}</tbody>;
};

const TableRow: React.FC<TableRowProps> = ({ children, className }) => {
  return <tr className={className}>{children}</tr>;
};

// TableCell Component (handles both th and td)
const TableCell: React.FC<TableCellProps> = ({
  children,
  isHeader = false,
  className,
  colSpan,
  rowSpan,
  ...rest
}) => {
  const CellTag = isHeader ? "th" : "td";

  return (
    <CellTag
      className={className}
      colSpan={colSpan}
      rowSpan={rowSpan}
      {...rest}
    >
      {children}
    </CellTag>
  );
};

export { Table, TableHeader, TableBody, TableRow, TableCell };
