package org.medtech.springbootjobmonitor.response;


import java.time.LocalDateTime;


public class JobExecutionFilter {
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String sortBy = "executionTime";
    private String sortDirection = "desc";
    private int page = 0;
    private int size = 10;

    public JobExecutionFilter() {}

    public JobExecutionFilter(String status, LocalDateTime startDate, LocalDateTime endDate,
                              String sortBy, String sortDirection, int page, int size) {
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sortBy = sortBy != null ? sortBy : "executionTime";
        this.sortDirection = sortDirection != null ? sortDirection : "desc";
        this.page = page;
        this.size = size;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}