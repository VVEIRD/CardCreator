# Card Creator

This project is for creating the front and backside image of cards with help of an csv file.

![Main Window](https://raw.githubusercontent.com/VVEIRD/Images/master/CardCreator/MW-Display.jpg)

## Features

* Front / rear image for the card
* Alternate rear image, if the rear image is not changed
* Custom fonts
* Import/Export of projects
* Visual Mapping editor for adding and editing fields to the card
* Scripting support for calculated fields using either a mapped csv column or all columns of a row
* Importing csv
   * Support for bold and itallic using the tags &lt;b&gt;&lt;/b&gt; and &lt;i>&lt;/i&gt;
   * Support for Images using the tag &lt;img>&lt;/img&gt;
   * Linebreaks can be added by inserting "-n-" in any text line.
   * The possibility to keep image references or to include the images into the csv files as base64 encoded strings.
   * Custom delimiter for the csv file
* Parallel creation of the card files

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
      - If the field is computed via javascript
5. Import csv file (delimiter can be changed by going to "edit -> configuration")
   - CSV columns can contain images by including the following:
     - Start tag <img>: and the end tag </img>, e.g. <img>/home/bla/img.png</img>
     - Start tag <imgb> and end tag </imgb> for base64-encoded images, e.g. <imgb>BASE64_ENCODED_IMG</imgb>
   - Text can now be __bold__ and _italic_ aswell as **_bold and itallic_**. Use the Tags &lt;b&gt;&lt;/b&gt; for bold and &lt;i>&lt;/i&gt; for italic
   - CSV columns can contain linebreaks that are splitting paragraphs by using '-n-', e.g. This is the first paragraph.-n-This is the second paragraph.
6. Map fields to csv columns
7. Create the cards

**ToDo**

- [x] Implementing the image content (Not tested yet)
