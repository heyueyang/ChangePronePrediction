dir_path=$1
filelist=`ls $dir_path`


for file in ${filelist[@]}
do 
	echo "------"$file"-------"
	sudo mysql --local-infile -uroot -p111111 -e "DROP DATABASE IF EXISTS "$file";CREATE DATABASE IF NOT EXISTS "$file" DEFAULT CHARSET utf8 COLLATE utf8_general_ci;"
	cd $dir_path"/"$file
	sudo miningit -uroot -p111111 -d$file --hard-order --extension=BugFixMessage,FileTypes,Content,Patches,PatchLOC,Hunks,HunkBlame
	sudo mysql -uroot -p111111 -e "use "$file";alter table content drop column content;"
	sudo mysql -uroot -p111111 -e "use "$file";CREATE VIEW content_loc(id,commit_id,file_id,loc) AS SELECT  id,commit_id,file_id,loc FROM content"
	echo "------min "$file" finished!-------"
	cd ../
done

