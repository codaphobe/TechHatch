import React from 'react'
import {
    Pagination,
    PaginationContent,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
  } from "../ui/pagination";
  import { Separator } from '../ui/separator'

const PaginationComponent = ({page, totalPages, onPageChange}) => {
  return (
    <Pagination >
        <PaginationContent className='gap-0.5'>
            <PaginationItem>
            <PaginationPrevious 
                className="h-7 p-4 text-md border bg-card" 
                onClick={() => onPageChange(Math.max(1, page-1))}/>
            </PaginationItem>

                {Array.from({length: totalPages}).map((_,idx) => {
                    const p = idx + 1;
                    return(
                        <div key={idx} className="flex items-center">
                            <PaginationItem  className="px-3">
                                <PaginationLink
                                    isActive={page === p}
                                    onClick={() => onPageChange(p)}>
                                    {p}
                                </PaginationLink>
                            </PaginationItem>
                            { p < totalPages && 
                                <Separator orientation="vertical" className="h-6 self-center ml-0.5"/>}
                        </div>
                    );
                })}
                
            <PaginationItem>
                <PaginationNext 
                    className="h-7 p-4 text-md border bg-card border-border" 
                    onClick={() => onPageChange(Math.min(totalPages, page+1))} />
            </PaginationItem>
        </PaginationContent>
    </Pagination>
  )
}

export default PaginationComponent;