/*
 * Copyright (C) 2011 - 2015, MyWarp team and contributors
 *
 * This file is part of MyWarp.
 *
 * MyWarp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyWarp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyWarp. If not, see <http://www.gnu.org/licenses/>.
 */

package me.taylorkelly.mywarp.bukkit.util;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.ChatColor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Provides several static methods that allow basic formatting of strings send to players. Colors are supported, bold
 * and italic formatting not!
 * <p/>
 * The basic character widths this class relies on where extracted from old Minecraft source code. The algorithm and
 * supported characters in Minecraft have changed, but these changes are not covered!
 */
public final class FormattingUtils {

  /**
   * The horizontal width of a chat line in vanilla Minecraft.
   */
  private static final int CHAT_WIDTH = 318; // 325

  /**
   * The character used to indicate formatting-codes.
   */
  private static final char FORMATTING_CHAR = ChatColor.COLOR_CHAR;

  /**
   * A map that stores supported characters and their width in the Minecraft chat. This map is a conversion of code
   * originally found in older Minecraft versions. It does not include most unicode characters that are supported
   * today!
   */
  private static final Map<Character, Integer>
      CHAR_WIDTHS =
      ImmutableMap.<Character, Integer>builder().put(' ', 4).put('!', 2).put('"', 5).put('#', 6).put('$', 6).put('%', 6)
          .put('&', 6).put('\'', 3).put('(', 5).put(')', 5).put('*', 5).put('+', 6).put(',', 2).put('-', 6).put('.', 2)
          .put('/', 6).put('0', 6).put('1', 6).put('2', 6).put('3', 6).put('4', 6).put('5', 6).put('6', 6).put('7', 6)
          .put('8', 6).put('9', 6).put(':', 2).put(';', 2).put('<', 5).put('=', 6).put('>', 5).put('?', 6).put('@', 7)
          .put('A', 6).put('B', 6).put('C', 6).put('D', 6).put('E', 6).put('F', 6).put('G', 6).put('H', 6).put('I', 4)
          .put('J', 6).put('K', 6).put('L', 6).put('M', 6).put('N', 6).put('O', 6).put('P', 6).put('Q', 6).put('R', 6)
          .put('S', 6).put('T', 6).put('U', 6).put('V', 6).put('W', 6).put('X', 6).put('Y', 6).put('Z', 6).put('[', 4)
          .put('\\', 6).put(']', 4).put('^', 6).put('_', 6).put('a', 6).put('b', 6).put('c', 6).put('d', 6).put('e', 6)
          .put('f', 5).put('g', 6).put('h', 6).put('i', 2).put('j', 6).put('k', 5).put('l', 3).put('m', 6).put('n', 6)
          .put('o', 6).put('p', 6).put('q', 6).put('r', 6).put('s', 6).put('t', 4).put('u', 6).put('v', 6).put('w', 6)
          .put('x', 6).put('y', 6).put('z', 6).put('{', 5).put('|', 2).put('}', 5).put('~', 7).put('⌂', 6).put('Ç', 6)
          .put('ü', 6).put('é', 6).put('â', 6).put('ä', 6).put('à', 6).put('å', 6).put('ç', 6).put('ê', 6).put('ë', 6)
          .put('è', 6).put('ï', 4).put('î', 6).put('ì', 3).put('Ä', 6).put('Å', 6).put('É', 6).put('æ', 6).put('Æ', 6)
          .put('ô', 6).put('ö', 6).put('ò', 6).put('û', 6).put('ù', 6).put('ÿ', 6).put('Ö', 6).put('Ü', 6).put('ø', 6)
          .put('£', 6).put('Ø', 6).put('×', 4).put('ƒ', 6).put('á', 6).put('í', 3).put('ó', 6).put('ú', 6).put('ñ', 6)
          .put('Ñ', 6).put('ª', 6).put('º', 6).put('¿', 6).put('®', 7).put('¬', 6).put('½', 6).put('¼', 6).put('¡', 2)
          .put('«', 6).put('»', 6).put(FORMATTING_CHAR, 0).build();

  /**
   * Block initialization of this class.
   */
  private FormattingUtils() {
  }

  /**
   * Gets the width of the given character. Will return 0 if the character is not displayed or not covered by the
   * underling data.
   *
   * @param character the character to check
   * @return the width of the character in pixels
   */
  public static int getWidth(char character) {
    int width = 0;
    if (CHAR_WIDTHS.containsKey(character)) {
      width = CHAR_WIDTHS.get(character);
    }
    return width;
  }

  /**
   * Gets the width of the given string. Formatting codes are ignored.
   *
   * @param str the string to check
   * @return the width of the string in pixels
   */
  public static int getWidth(String str) {
    int width = 0;

    str = ChatColor.stripColor(str);
    for (char c : str.toCharArray()) {
      width += getWidth(c);
    }
    return width;
  }

  /**
   * Calls {@link #paddingRight(String, char)} using a space as padding character.
   *
   * @param str the string
   * @return the padded string
   */
  public static String paddingRight(String str) {
    return paddingRight(str, ' ');
  }

  /**
   * Calls {@link #paddingRight(String, char, int)} using the chat width as paddedWidth.
   *
   * @param str the string
   * @param pad the padding char
   * @return the padded string
   */
  public static String paddingRight(String str, char pad) {
    return paddingRight(str, pad, CHAT_WIDTH);
  }

  /**
   * Pads the given string with the given character on the right until the string has the given width.
   *
   * @param str         the string
   * @param pad         the padding char
   * @param paddedWidth the width of the padded string
   * @return the padded string
   */
  public static String paddingRight(String str, char pad, int paddedWidth) {
    paddedWidth -= getWidth(str);
    return StringUtils.rightPad(str, paddedWidth / getWidth(pad), pad);
  }

  /**
   * Calls {@link #paddingLeft(String, char)} using a space as padding char.
   *
   * @param str the string
   * @return the padded string
   */
  public static String paddingLeft(String str) {
    return paddingLeft(str, ' ');
  }

  /**
   * Calls {@link #paddingLeft(String, char, int)} using the chat width as paddedWidth.
   *
   * @param str the string
   * @param pad the padding char
   * @return the padded string
   */
  public static String paddingLeft(String str, char pad) {
    return paddingLeft(str, pad, CHAT_WIDTH);
  }

  /**
   * Pads the given string with the given character on the left until the string has the given width.
   *
   * @param str         the string
   * @param pad         the padding character
   * @param paddedWidth the width of the padded string
   * @return the padded string
   */
  public static String paddingLeft(String str, char pad, int paddedWidth) {
    paddedWidth -= getWidth(str);
    return StringUtils.leftPad(str, paddedWidth / getWidth(pad), pad);
  }

  /**
   * Calls {@link #center(String, char)} using a space as padding character.
   *
   * @param str the string
   * @return the centered string
   */
  public static String center(String str) {
    return center(str, ' ');
  }

  /**
   * Calls {@link #center(String, char, int)} using the chat width as paddedWidth.
   *
   * @param str the string
   * @param pad the padding char
   * @return the centered string
   */
  public static String center(String str, char pad) {
    return center(str, pad, CHAT_WIDTH);
  }

  /**
   * Centralizes the given string relative to the given width by padding it with the given character.
   *
   * @param str         the string to centralize
   * @param pad         the padding char
   * @param paddedWidth the width of the padded string
   * @return the centered string
   */
  public static String center(String str, char pad, int paddedWidth) {
    paddedWidth -= getWidth(str);
    String padding = StringUtils.repeat(Character.toString(pad), paddedWidth / getWidth(pad) / 2);
    return padding + str + padding;
  }

  /**
   * Calls {@link #trim(String, int)} using the chat width as trimmedWidth.
   *
   * @param str the string to trim
   * @return the trimmed string
   */
  public static String trim(String str) {
    return trim(str, CHAT_WIDTH);
  }

  /**
   * Trims the given String until it has the given width. Formatting codes are recognized and fully removed.
   *
   * @param str          the string to trim
   * @param trimmedWidth the width of the trimmed string
   * @return the trimmed string
   */
  public static String trim(String str, int trimmedWidth) {
    char[] chars = str.toCharArray();
    for (int i = chars.length - 1; i > 0; i--) {

      // also cut off color codes
      if (chars[i - 1] == FORMATTING_CHAR) {
        --i;
      }
      String check = String.valueOf(chars, 0, i);
      if (getWidth(check) <= trimmedWidth) {
        return check;
      }
    }
    return "";
  }

  /**
   * Calls {@link #wrap(String, String)} with an empty newLinePrefix.
   *
   * @param str the string to wrap
   * @return the wrapped string
   */
  public static String wrap(String str) {
    return wrap(str, "");
  }

  /**
   * Calls {@link #wrap(String, String, int)} using the chat width as totalWidth.
   *
   * @param str           the string to wrap
   * @param newLinePrefix the prefix for created new lines
   * @return the wrapped string
   */
  public static String wrap(String str, String newLinePrefix) {
    return wrap(str, newLinePrefix, CHAT_WIDTH);
  }

  /**
   * Wraps the given String into multiple lines so that each line is at most as wide as the given width. Each new line
   * will start with the given prefix.
   *
   * @param str           the string to wrap
   * @param newLinePrefix the prefix for created new lines
   * @param wrappedWidth  the width of each line
   * @return the wrapped string
   */
  public static String wrap(String str, String newLinePrefix, int wrappedWidth) {
    return wrappedJoin(Splitter.on(' ').trimResults().omitEmptyStrings().split(str), newLinePrefix, wrappedWidth);
  }

  /**
   * Joins the given parts, separated by blanks. The resulting string will be wrapped into multiple lines so that each
   * line is at most as wide as the given width. Each new line will start with the given prefix.
   *
   * @param parts         the parts
   * @param newLinePrefix the prefix for created new lines
   * @param wrappedWidth  the width of each line
   * @return the wrapped string
   */
  private static String wrappedJoin(Iterable<String> parts, String newLinePrefix, int wrappedWidth) {
    StrBuilder ret = new StrBuilder();

    StrBuilder line = new StrBuilder();
    for (String part : parts) {
      // if the word is longer than the maximum length, add chars as long
      // as possible than make a new line
      if (getWidth(part) > wrappedWidth) {
        for (char c : part.toCharArray()) {
          // if the maximum width is reached, make a new line
          if (getWidth(line.toString()) + getWidth(c) > wrappedWidth) {
            ret.appendln(line.toString());
            line.clear();
            line.append(newLinePrefix);
          }
          line.append(c);
        }
        // if the world AND the needed black is longer than the maximum
        // width make a new line and insert the word there
      } else {
        // if the maximum width is reached, make a new line
        if (getWidth(line.toString()) + getWidth(part) + getWidth(' ') > wrappedWidth) {
          ret.appendln(line.toString());
          line.clear();
          line.append(newLinePrefix);
        }
        // a blank is only needed if the line is not empty AND the last
        // char is not a a blank (e.g. from prefix)
        if (!line.isEmpty() && !line.rightString(1).equals(" ")) {
          line.append(' ');
        }
        line.append(part);
      }
    }
    // commit the line
    if (!line.isEmpty()) {
      ret.append(line);
    }
    return ret.toString();
  }

  /**
   * Calls {@link #twoColumnAlign(String, String, char)} using a space as padding char.
   *
   * @param leftColumn  the contents of the left column
   * @param rightColumn the contents of the right column
   * @return the formatted string
   */
  public static String twoColumnAlign(String leftColumn, String rightColumn) {
    return twoColumnAlign(leftColumn, rightColumn, ' ');
  }

  /**
   * Calls {@link #twoColumnAlign(String, String, char, int)} using the chat width as totalWidth.
   *
   * @param leftColumn  the contents of the left column
   * @param rightColumn the contents of the right column
   * @param pad         the padding character
   * @return the formatted string
   */
  public static String twoColumnAlign(String leftColumn, String rightColumn, char pad) {
    return twoColumnAlign(leftColumn, rightColumn, pad, CHAT_WIDTH);
  }

  /**
   * Creates a two-column-layout from the given strings, using the given character as padding in between with the given
   * total width. The left column will be aligned on the left, the right column on the right of the given width.
   *
   * @param leftColumn  the contents of the left column
   * @param rightColumn the contents of the right column
   * @param pad         the padding character
   * @param totalWidth  the horizontal width that should be covered by the layout
   * @return the formatted string
   */
  public static String twoColumnAlign(String leftColumn, String rightColumn, char pad, int totalWidth) {
    int leftWidth = getWidth(leftColumn);
    int rightWidth = getWidth(rightColumn);
    totalWidth -= leftWidth + rightWidth;

    if (totalWidth > 0) {
      return leftColumn + StringUtils.repeat(Character.toString(pad), totalWidth / getWidth(pad)) + rightColumn;
    }
    // If both columns together are larger than the totalWidth, the larger
    // one is trimmed until it fits.
    int separatorWidth = getWidth(pad);
    totalWidth -= separatorWidth;
    if (leftWidth > rightWidth) {
      leftColumn = trim(leftColumn, leftWidth + totalWidth);
    } else {
      rightColumn = trim(rightColumn, rightWidth + totalWidth);
    }
    return leftColumn + pad + rightColumn;
  }

  /**
   * Calls {@link #toList(char, List)} using a '-' as list character.
   *
   * @param entries the list's entries
   * @return a string with all entries
   */
  public static String toList(List<String> entries) {
    return toList('-', entries);
  }

  /**
   * Calls {@link #toList(char, int, List)} using the chat width as totalWidth.
   *
   * @param listChar the character that will be displayed as bullet point before each entry
   * @param entries  the list's entries
   * @return a string with all entries
   */
  public static String toList(char listChar, List<String> entries) {
    return toList(listChar, CHAT_WIDTH, entries);
  }

  /**
   * Creates a not numbered list from the given strings. Each string represents an independent entry on the list.
   * Strings that are longer than the given width will be split above several lines.
   *
   * @param listChar the character that will be displayed as bullet point before each entry
   * @param maxWidth the maximal width of each entry
   * @param entries  the list's entries
   * @return the list
   */
  public static String toList(char listChar, int maxWidth, List<String> entries) {
    StrBuilder ret = new StrBuilder();
    String listPrefix = listChar + " ";
    Splitter splitter = Splitter.on(' ').trimResults().omitEmptyStrings();

    for (String entry : entries) {

      if (!ret.isEmpty()) {
        ret.appendNewLine();
        // reset colors from the previous entry
        ret.append(ChatColor.RESET);
      }

      List<String> entryParts = new LinkedList<String>();
      entryParts.add(listPrefix);
      Iterables.addAll(entryParts, splitter.split(entry));

      ret.append(wrappedJoin(entryParts, "  ", maxWidth));
    }

    return ret.toString();
  }
}
