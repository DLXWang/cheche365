  package com.cheche365.cheche.operationcenter.web.model;

import java.util.List;

  public class DataTablesPageViewModel<T> {

      private Long iTotalRecords;//实际的行数
      private Long iTotalDisplayRecords;//过滤之后，实际的行数
      private Integer draw;//datatables传过来的参数 原样返回
      private List<T> aaData;//返回实体
      private T data;

      public DataTablesPageViewModel(List<T> aaData, T data)  {
          this.aaData = aaData;
          this.data = data;
      }

      public DataTablesPageViewModel(List<T> aaData) {
          this.aaData = aaData;
      }
      public DataTablesPageViewModel(Long iTotalRecords, Long iTotalDisplayRecords, Integer draw, List<T> aaData) {
          this.iTotalRecords = iTotalRecords;
          this.iTotalDisplayRecords = iTotalDisplayRecords;
          this.draw = draw;
          this.aaData = aaData;
      }
      public DataTablesPageViewModel(Long iTotalRecords, Long iTotalDisplayRecords, Integer draw, List<T> aaData, T data) {
          this.iTotalRecords = iTotalRecords;
          this.iTotalDisplayRecords = iTotalDisplayRecords;
          this.draw = draw;
          this.aaData = aaData;
          this.data = data;
      }

      public DataTablesPageViewModel() {
      }

      public Long getiTotalRecords() {
          return iTotalRecords;
      }

      public void setiTotalRecords(long iTotalRecords) {
          this.iTotalRecords = iTotalRecords;
      }

      public Long getiTotalDisplayRecords() {
          return iTotalDisplayRecords;
      }

      public void setiTotalDisplayRecords(Long iTotalDisplayRecords) {
          this.iTotalDisplayRecords = iTotalDisplayRecords;
      }


      public Integer getDraw() { return draw;  }

      public void setDraw(Integer draw) {  this.draw = draw; }

      public List<T> getAaData() {
          return aaData;
      }

      public void setAaData(List<T> aaData) {
          this.aaData = aaData;
      }

      public void setiTotalRecords(Long iTotalRecords) {    this.iTotalRecords = iTotalRecords;  }

      public T getData() {   return data; }

      public void setData(T data) { this.data = data; }
  }
