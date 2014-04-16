scp rkrish20@mllab:/home/rkrish20/EdgeMerger/mergeEdges.sql .
/Applications/pgAdmin3.app/Contents/SharedSupport/psql -h localhost -U postgres -d runkeeper_chicago_0_01 -f addSimilarToEdgeColumn.sql
/Applications/pgAdmin3.app/Contents/SharedSupport/psql -h localhost -U postgres -d runkeeper_chicago_0_01 -f mergeEdges.sql
