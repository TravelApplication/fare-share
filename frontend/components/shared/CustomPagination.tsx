'use client';
import React from 'react';
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationPrevious,
  PaginationLink,
  PaginationEllipsis,
  PaginationNext,
} from '../ui/pagination';
import { useSearchParams } from 'next/navigation';

interface CustomPaginationProps {
  totalTrips: number;
  tripsPerPage: number;
}

function CustomPagination({ totalTrips, tripsPerPage }: CustomPaginationProps) {
  const totalPages = Math.ceil(totalTrips / tripsPerPage);
  const searchParams = useSearchParams();
  const currentPage = Number(searchParams.get('page')) || 1;

  if (totalPages <= 1) return null;

  return (
    <Pagination className="text-small-regular mt-4">
      <PaginationContent>
        <PaginationItem className="shadow-md rounded-lg">
          {currentPage > 1 && (
            <PaginationPrevious href={`?page=${currentPage - 1}`} />
          )}
        </PaginationItem>

        {[...Array(totalPages)].map((_, index) => {
          const page = index + 1;
          if (
            page === 1 ||
            page === totalPages ||
            Math.abs(page - currentPage) <= 1
          ) {
            return (
              <PaginationItem key={page} className="shadow-md rounded-lg">
                <PaginationLink
                  href={`?page=${page}`}
                  isActive={page === currentPage}
                >
                  {page}
                </PaginationLink>
              </PaginationItem>
            );
          }
          if (Math.abs(page - currentPage) === 2) {
            return (
              <PaginationItem key={page} className="shadow-md rounded-lg">
                <PaginationEllipsis />
              </PaginationItem>
            );
          }
          return null;
        })}

        <PaginationItem className="shadow-md rounded-lg">
          {currentPage < totalPages && (
            <PaginationNext href={`?page=${currentPage + 1}`} />
          )}
        </PaginationItem>
      </PaginationContent>
    </Pagination>
  );
}

export default CustomPagination;
