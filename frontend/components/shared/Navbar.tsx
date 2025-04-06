'use client';
import React, { useEffect, useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '../ui/dropdown-menu';
import { Button } from '../ui/button';
import { Avatar, AvatarFallback } from '../ui/avatar';
import { Bell, LogOut, Plane, User } from 'lucide-react';
import { getToken, isLoggedIn, logout } from '@/lib/auth';
import axios from 'axios';
import { appStore } from '@/store/appStore';

function Navbar() {
  const [token, setToken] = useState<string | null>(null);
  const [notificationAmount, setNotificationAmount] = useState<number>(0);

  const toFetchFriendInvitations = appStore((s) => s.toFetchFriendInvitations);
  const setToFetchFriendInvitations = appStore(
    (s) => s.setToFetchFriendInvitations,
  );
  const toFetchGroupInvitations = appStore((s) => s.toFetchGroupInvitations);
  const setToFetchGroupInvitations = appStore(
    (s) => s.setToFetchGroupInvitations,
  );

  const fetchNotifications = async () => {
    const token = getToken();
    if (!token) return;

    try {
      const [friendsRes, groupsRes] = await Promise.all([
        axios.get('/api/v1/friend-invitations/received', {
          headers: { Authorization: `Bearer ${token}` },
        }),
        axios.get('/api/v1/group-invitations/received', {
          headers: { Authorization: `Bearer ${token}` },
        }),
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
  }, []);

  useEffect(() => {
    if (toFetchFriendInvitations || toFetchGroupInvitations) {
      fetchNotifications();
      if (toFetchFriendInvitations) setToFetchFriendInvitations(false);
      if (toFetchGroupInvitations) setToFetchGroupInvitations(false);
    }
  }, [toFetchFriendInvitations, toFetchGroupInvitations]);

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

              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Avatar className="cursor-pointer">
                    <AvatarFallback>XX</AvatarFallback>
                  </Avatar>
                </DropdownMenuTrigger>
                <DropdownMenuContent>
                  <DropdownMenuLabel>Account</DropdownMenuLabel>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem>
                    <Link className="navbar_link" href="/account">
                      <User />
                      <p>Profile</p>
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem>
                    <Link className="navbar_link" href="/" onClick={logout}>
                      <LogOut />
                      <p>Log out</p>
                    </Link>
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
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
