from PIL import Image

from .helper import PillowTestCase

PIL151 = b"""
#define basic_width 32
#define basic_height 32
static char basic_bits[] = {
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
0x00, 0x00, 0x00, 0x00,
0x80, 0xff, 0xff, 0x01, 0x40, 0x00, 0x00, 0x02,
0x20, 0x00, 0x00, 0x04, 0x20, 0x00, 0x00, 0x04, 0x10, 0x00, 0x00, 0x08,
0x10, 0x00, 0x00, 0x08,
0x10, 0x00, 0x00, 0x08, 0x10, 0x00, 0x00, 0x08,
0x10, 0x00, 0x00, 0x08, 0x10, 0x00, 0x00, 0x08, 0x10, 0x00, 0x00, 0x08,
0x20, 0x00, 0x00, 0x04,
0x20, 0x00, 0x00, 0x04, 0x40, 0x00, 0x00, 0x02,
0x80, 0xff, 0xff, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
0x00, 0x00, 0x00, 0x00,
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
0x00, 0x00, 0x00, 0x00,
};
"""


class TestFileXbm(PillowTestCase):
    def test_pil151(self):
        from io import BytesIO

        im = Image.open(BytesIO(PIL151))

        im.load()
        self.assertEqual(im.mode, "1")
        self.assertEqual(im.size, (32, 32))

    def test_open(self):
        # Arrange
        # Created with `convert hopper.png hopper.xbm`
        filename = "Tests/images/hopper.xbm"

        # Act
        im = Image.open(filename)

        # Assert
        self.assertEqual(im.mode, "1")
        self.assertEqual(im.size, (128, 128))

    def test_open_filename_with_underscore(self):
        # Arrange
        # Created with `convert hopper.png hopper_underscore.xbm`
        filename = "Tests/images/hopper_underscore.xbm"

        # Act
        im = Image.open(filename)

        # Assert
        self.assertEqual(im.mode, "1")
        self.assertEqual(im.size, (128, 128))
