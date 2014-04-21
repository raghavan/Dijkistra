/Applications/pgAdmin3.app/Contents/SharedSupport/psql -h localhost -U postgres -d runkeeper_chicago_0_01 -f getHausdorffDistanceFromDB.sql > HausdorffDistance_table_result.txt
cat HausdorffDistance_table_result.txt  | awk '{print $1, $3, $5, $7, $9}' > HausdorffDistance.txt 
scp HausdorffDistance.txt rkrish20@mllab:/home/rkrish20/EdgeMerger/.
