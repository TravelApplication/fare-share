import React from 'react';
import { UserSearch } from '@/validation/userProfileSchemas';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

function UserTile({
  user,
  onClick,
  showInviteButton = false,
  onInvite,
}: {
  user: UserSearch;
  onClick?: () => void;
  showInviteButton?: boolean;
  onInvite?: () => void;
}) {
  return (
    <div
      onClick={onClick}
      className="flex justify-between items-center p-2 rounded-lg cursor-pointer hover:bg-gray-100"
    >
      <div className="flex items-center">
        <Avatar className="w-12 h-12 shadow-md mr-2">
          <AvatarImage src="" />
          <AvatarFallback className="text-primary-500 text-[1rem]">
            {user.firstName.charAt(0)}
          </AvatarFallback>
        </Avatar>
        <h3 className="font-semibold">
          {user.firstName} {user.lastName}
        </h3>
      </div>
      {showInviteButton && (
        <button
          onClick={(e) => {
            e.stopPropagation(); // zapobiega onClick z całego diva
            onInvite?.();
          }}
          className="text-sm px-3 py-1 bg-primary-500 text-white rounded-lg hover:bg-primary-600"
        >
          Invite
        </button>
      )}
    </div>
  );
}

export default UserTile;
