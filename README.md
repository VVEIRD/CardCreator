# Card Creator

This project is for creating the front and backside image of cards with help of an csv file.

**HowTo**

1. Create a project, 
2. Load front and rear image for the card.
3. Add custom font if wanted
4. Add named fields for text/images by using eidt -> Manage Fields
   1. Select the first corner of the field onm the image
   2. Select the second corner of the field
   3. Input additional details like 
      - Name
      - Card side
      - Content (text-left/right/center/block, image)
      - Font
      - Fontsize
      - If paragraph should be indented
      - If the text should be resized to fit the field width.
5. Import csv file (ATM locked to ';' as field delimiter, '"' for Text enclosure.)
   - CSV columns can contain images by including the following:
     - Start tag -img: and the end tag :img-, e.g. -img:/home/bla/img.png:img-
     - Start tag -imgb: and end tag :imgb- for base64-encoded images, e.g. -imgb:BASE64_ENCODED_IMG:imgb-
   - CSV columns can contain linebreaks that are splitting paragraphs by using '-n-', e.g. This is the first paragraph.-n-This is the second paragraph.
6. Map fields to csv columns
7. Create the cards

**ToDo**

- [ ] Implementing the image content
- [ ] ???
