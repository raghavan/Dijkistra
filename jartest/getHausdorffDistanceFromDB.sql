select a1.id,a2.id,a1.source,a2.target,ST_HausdorffDistance(a1.the_geom,a2.the_geom) 
from activity_linestrings_edge_table_noded a1, activity_linestrings_edge_table_noded a2 where a1.source = a2.source and a1.target = a2.target;
