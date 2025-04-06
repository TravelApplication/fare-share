'use client';

import { getToken, logout } from '@/lib/auth';
import { appStore } from '@/store/appStore';
import axios from 'axios';
import { useEffect } from 'react';

function Page() {
  const toFetchFriendInvitations = appStore(
    (state) => state.toFetchFriendInvitations,
  );
  const setToFetchFriendInvitations = appStore(
    (state) => state.setToFetchFriendInvitations,
  );

  const fetchInvitations = async () => {
    const token = getToken();
    if (!token) {
      logout();
      return;
    }

    try {
      const response = await axios.get('/api/v1/friend-invitations/received', {
        headers: { Authorization: `Bearer ${token}` },
      });
      console.log('response', response.data);
    } catch (error) {
      console.error('Error fetching friend invitations:', error);
    }
  };

  useEffect(() => {
    fetchInvitations();
  }, []);

  useEffect(() => {
    if (toFetchFriendInvitations) {
      fetchInvitations();
      setToFetchFriendInvitations(false);
    }
  }, [toFetchFriendInvitations, setToFetchFriendInvitations]);

  return <div>Notifications!!!!!</div>;
}
export default Page;
