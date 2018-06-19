# Find-file-difference

Find modified files in the same folders and copy from the first folder to the second. If you need to backup unmodified files to another folder.

Сравнивание двух папок с файлами, если какие то файлы были модифицорованы, то замена на новые. Можно делать бэкап старых файлов в отдельную папку.

Usage:
```bat
"java -jar ${some_name_after_build}.jar folder1;folder2;folder3"
```
- folder1 - First main folder.
- folder2 - The second folder in which you want to make changes from the first.
- folder3 - Folder to backup.
