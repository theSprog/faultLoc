抹去 safe value 对 FIC 的影响

- 有可能定位不到错误
  - 错误: (1,-,-,-) 和 (-,1,-,-)
  - (0,1,1,1) -> 定位不出,因为在对第1个进行 mutate 时引入了新故障
  - 从而 pending 也计算不准确了
- 有可能定位到错误的重叠部分
  - 错误: (1,1,-,-) 和 (1,-,1,-)
  - (1,1,1,1) -> 定位到重叠

-----

即使有非单因子故障,FIC 也可能 pending 为 0

i.e.
错误: (1,1,-,-)
测试用例: (1,1,1,1)

通过: (1,-,1,1) 和 (-,1,1,1)

MinFault: (1,1,-,-)

pendingSize = 0

------

AIFL > 15, ComFIL > 15, TRT > 24, SP > 100 都不可做

AIFL 的 Threshold 设置为参数 N
SP 的 degree 设置为 2

precision 的 - 代表不可做, NAN 代表分母为 0 
recall 的 - 代表不可做

additional testcase 的计算方式是将所有错误测试用例所需要的的additional testcase总和相加除以错误测试用例的数目，
即平均一条测试用例需要多少additional testcase

execTime 的单位是毫秒



没有使用 CTA，因为它是 non-adaptive 方法，加在 adaptive 方法中显得有些不伦不类的

OFOT类方法只实现了 SOFOT，而没有实现 OFOT

pending 的算法是对每一条错误测试用例求pending数目，再求对于错误测试用例的平均
求 pending 时禁用了safe value
对于LG和SP(2),由于 health schema 太多，直接默认所有的 pairwise 都是对的，这引入一些误差，但不太多

容斥原理计算pending,选择的项数:
[0,20):全选
[20,60):5
[60,100):4
[100,-):3

