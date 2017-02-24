/**
 * Copyright (C) 2017 Kaj Magnus Lindberg
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.debiki.core

import com.debiki.core.Prelude._


case class MayWhat(
  mayEditPage: Boolean = false,
  mayEditComment: Boolean = false,
  mayEditWiki: Boolean = false,
  mayDeletePage: Boolean = false,
  mayDeleteComment: Boolean = false,
  mayCreatePage: Boolean = false,
  mayPostComment: Boolean = false,
  maySee: Boolean = false,
  debugCode: String = "") {

  require(maySee || (
    !mayEditPage && !mayEditComment && !mayEditWiki && !mayDeletePage &&
    !mayDeleteComment && !mayCreatePage && !mayPostComment), "EdE2WKB5FD")

  def addRemovePermissions(permissions: PermsOnPages, debugCode: String) = MayWhat(
    mayEditPage = permissions.mayEditPage.getOrElse(mayEditPage),
    mayEditComment = permissions.mayEditComment.getOrElse(mayEditComment),
    mayEditWiki = permissions.mayEditWiki.getOrElse(mayEditWiki),
    mayDeletePage = permissions.mayDeletePage.getOrElse(mayDeletePage),
    mayDeleteComment = permissions.mayDeleteComment.getOrElse(mayDeleteComment),
    mayCreatePage = permissions.mayCreatePage.getOrElse(mayCreatePage),
    mayPostComment = permissions.mayPostComment.getOrElse(mayPostComment),
    maySee = permissions.maySee.getOrElse(maySee),
    debugCode)

  def copyAsDeleted: MayWhat = copy(
    mayEditPage = false,
    mayEditComment = false,
    mayEditWiki = false,
    mayDeletePage = false,
    mayDeleteComment = false,
    mayCreatePage = false,
    mayPostComment = false)
}


object MayWhat {

  def mayNotSee(debugCode: String) = MayWhat(
    mayEditPage = false, mayEditComment = false, mayEditWiki = false,
    mayDeletePage = false, mayDeleteComment = false, mayCreatePage = false,
    mayPostComment = false, maySee = false, debugCode)

}


case class PermsOnPages(
  id: PermissionId,
  forPeopleId: UserId,
  onWholeSite: Option[Boolean],
  onCategoryId: Option[Int],
  onPageId: Option[PageId],
  onPostId: Option[PostId],
  onTagId: Option[TagLabelId],
  mayEditPage: Option[Boolean],
  mayEditComment: Option[Boolean],
  mayEditWiki: Option[Boolean],
  mayDeletePage: Option[Boolean],
  mayDeleteComment: Option[Boolean],
  mayCreatePage: Option[Boolean],
  mayPostComment: Option[Boolean],
  maySee: Option[Boolean]) {

  // Later, perhaps:
  // pin/unpin
  // close and/or archive
  // unlist topic
  // recategorize and rename topics
  // split and merge topics
  // nofollow removed
  // upload images / other attachments

  require(forPeopleId >= User.LowestHumanMemberId, "EdE8G4HU2W")
  require(!onCategoryId.contains(NoCategoryId), "EdE8UGF0W2")
  require(!onPageId.exists(_.isEmpty), "EdE8UGF0W3")
  require(!onPostId.contains(NoPostId), "EdE8UGF0W4")
  require(!onTagId.contains(NoTagId), "EdE8UGF0W5")

  // This permission grants rights on exactly one thing.
  require(1 == onWholeSite.oneIfDefined + onCategoryId.oneIfDefined + onPageId.oneIfDefined +
    onPostId.oneIfDefined + onTagId.oneIfDefined, "EdE7LFK2R5")

  // This permission grants some right(s), it's not just everything-undefined.
  require(1 <= mayEditPage.oneIfDefined + mayEditComment.oneIfDefined + mayEditWiki.oneIfDefined +
    mayDeletePage.oneIfDefined + mayDeleteComment.oneIfDefined + mayCreatePage.oneIfDefined +
    mayPostComment.oneIfDefined + maySee.oneIfDefined, "EdE7PUK2W3")
}


// Later:

// case class PermsOnPeople(...) or PermsOnGroups(...)
// e.g. permissions to:
// - suspend someone (site wide perm, not per group),
// - @mention a group, e.g. let only trust-level >= Basic mention a group.
//     + mention owners only?  @group_name.owners or owners@group_name ? = may add/remove members
// - how many people to notify when group mentioned. If there's a support group with 100 people,
//     perhaps just notify 5 random people? or round-robin
// - or configure sub-mention groups, e.g. owners@group_name —> @amanda @bert @cicero
//       or  1st_line@support  or 2nd_line@support or 3rd_line@support
// - see that the group exists (e.g. perhaps only admins should see a @@penetration_testers group?)
// - configure group settings:
//   - edit full-name (but perhaps not the group's username)
//   - edit about-group
//   - edit group avatar
//   - add/remove group members (one needn't be a group member in order to manage it),
//   - allow/disable self-join and self-exit
//   - allow/disable apply-for-membership button  and approve/deny such request
//   - configure group-is-visible-to all users: y/n
// (inspired by https://meta.discourse.org/t/improving-the-groups-page-for-1-7/53347 )

// case class PermsOnTags(...)
// — could let only a certain group of people use some tags

// Probably never:
// case class PermsOnCategories(...)  // edit category description? rename slug?
// — they can just send a message to an admin and ask hen to do it instead.
