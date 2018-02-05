#bin/bash
#back up files
cp -f $1/resourcelist.json $1/bak/resourcelist_bak.json;
cp -f $1/totalrows.text $1/bak/totoalrows_bak.text;
cp -f $1/incremental_list.text $1/bak/incremental_list_bak.text;
cp -f $1/viewed_list.text $1/bak/viewed_list_bak.text;
cp -f $1/error.log $1/bak/error_bak.log;
cp -f $1/out.log $1/bak/out_bak.log;
