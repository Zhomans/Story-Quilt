Story-Quilt
=============

Mobile Prototyping Final Sprint: Android App based off of "3-word story"

###Importing Project
We are importing via gradle (which is why gradle files are here).
See STANDARDS.md


###To Do List
https://trello.com/b/1WRbHdIa/mobproto-storyquilt

###Firebase Data Structures
Story
```
id 
pieces[Piece] 
title 
age limit 
last updated 
text limit 
history limit 
priority (num_users) 
```
User
```
id 
name 
reports 
posts
writing[Story Ids] 
reading[Story Ids] 
banned[Story Ids]
age 
banned? 
```
Piece
```
id 
poster
date
text
```

Serializable Objects for each.
