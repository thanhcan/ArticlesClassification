ArticlesClassification
======================

Classification
  *) Bài toán phân lớp theo chủ đề sử dụng thuật toán Naïve Bayes
  *) Độ chính xác đạt được ~80%
  *) DATA:
      + " 0_DATA_Traning_Input "    : dữ liệu huấn luyện - n chủ đề
      + " 0_DATA_Traning_Output "   : kết quả huấn luyện - xác xuất P(Ci) mỗi chủ đề và P(Ci|x) với mỗi từ khóa trong chủ đề
          - Dòng 1: P(Ci)
          - Mỗi 2 dòng tiếp theo: từ khóa x
                                : P(Ci|x)
      + " 1_DATA_Inference_Input "  : dữ liệu dùng cần phân lớp - các bài báo
      + " 1_DATA_Inference_Output " : dữ liệu sau khi được phân lớp - các bài báo được chia vào các lớp cụ thể
  *) Lib:
      + Công cụ đánh dấu từ
