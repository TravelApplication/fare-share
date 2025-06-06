'use client';
import React, { useEffect, useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { Button } from '../ui/button';
import { Bell, Plane } from 'lucide-react';
import { getToken, isLoggedIn } from '@/lib/auth';
import SearchUsers from './SearchUsers';
import { appStore } from '@/store/appStore';
import { ProfileMenu } from './ProfileMenu';
import { User } from '@/validation/userProfileSchemas';
import { Membership } from '@/validation/membershipSchema';
import axiosInstance from '@/lib/axiosInstance';
import { Friend } from '@/validation/friendSchema';

function Navbar() {
  const [token, setToken] = useState<string | null>(null);
  const user = appStore((s) => s.user);
  const setUser = appStore((s) => s.setUser);
  const setFriends = appStore((s) => s.setFriends);

  const [notificationAmount, setNotificationAmount] = useState<number>(0);

  const toFetchFriendInvitations = appStore((s) => s.toFetchFriendInvitations);
  const setToFetchFriendInvitations = appStore(
    (s) => s.setToFetchFriendInvitations,
  );
  const toFetchGroupInvitations = appStore((s) => s.toFetchGroupInvitations);
  const setToFetchGroupInvitations = appStore(
    (s) => s.setToFetchGroupInvitations,
  );

  useEffect(() => {
    const token = getToken();
    setToken(token);

    if (token && !user) {
      const fetchUserData = async () => {
        try {
          const response = await axiosInstance.get('users');

          const userData: User = {
            id: response.data.id,
            email: response.data.email,
            userInfo: response.data.userInfo,
            memberships: response.data.memberships.map(
              (membership: Membership) => ({
                groupId: membership.groupId,
              }),
            ),
          };

          setUser(userData);
        } catch (err) {
          console.error('Failed to fetch user data:', err);
        }
      };

      const fetchFriends = async () => {
        try {
          const response = await axiosInstance.get('friendships');
          const friendsData = response.data.map((friend: Friend) => friend.id);

          setFriends(friendsData);
        } catch (err) {
          console.error('Failed to fetch friends:', err);
        }
      };

      fetchFriends();
      fetchUserData();
    }
  }, [token, user, setUser, setFriends]);

  const fetchNotifications = async () => {
    const token = getToken();
    if (!token) return;

    try {
      const [friendsRes, groupsRes] = await Promise.all([
        axiosInstance.get('friend-invitations/received'),
        axiosInstance.get('group-invitations/received'),
      ]);

      const count =
        (friendsRes.data?.length || 0) + (groupsRes.data?.length || 0);
      setNotificationAmount(count);
    } catch (err) {
      console.error('Failed to fetch notifications:', err);
    }
  };

  useEffect(() => {
    const token = getToken();
    setToken(token);
    if (token) {
      fetchNotifications();
    }
  }, [token]);

  useEffect(() => {
    if (toFetchFriendInvitations || toFetchGroupInvitations) {
      fetchNotifications();
      if (toFetchFriendInvitations) setToFetchFriendInvitations(false);
      if (toFetchGroupInvitations) setToFetchGroupInvitations(false);
    }
  }, [
    toFetchFriendInvitations,
    toFetchGroupInvitations,
    setToFetchFriendInvitations,
    setToFetchGroupInvitations,
  ]);

  return (
    <nav className="navbar">
      <div className="navbar_content">
        <Link
          href={token && isLoggedIn(token) ? '/trips' : '/'}
          className="-ml-1 flex items-center gap-1.5"
        >
          <Image src="/assets/logo.svg" alt="logo" width={44} height={44} />
          <p className="text-heading3-bold max-xs:hidden">FareShare</p>
        </Link>
        <div className="flex items-center gap-6">
          {token ? (
            <>
              <SearchUsers />
              <Link className="navbar_link" href="/trips">
                <Plane />
                <p className="max-sm:hidden">Trips</p>
              </Link>

              <Link className="navbar_link relative" href="/notifications">
                <div>
                  <Bell />
                  {notificationAmount > 0 && (
                    <div className="absolute -top-0.5 left-3 bg-primary-500 text-white text-xs font-bold rounded-full w-4 h-4 flex items-center justify-center">
                      {notificationAmount}
                    </div>
                  )}
                </div>
                <p className="max-sm:hidden">Invitations</p>
              </Link>

              <ProfileMenu user={user} />
            </>
          ) : (
            <>
              <Link className="navbar_link" href="/sign-in">
                Sign In
              </Link>
              <Button className="bg-primary-500 hover:bg-primary-600">
                <Link href="/sign-up">Sign Up - It&apos;s Free</Link>
              </Button>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
